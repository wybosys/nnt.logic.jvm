package com.nnt.server

import com.nnt.core.*
import com.nnt.session.ModelError
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.full.callSuspend

interface IRouterable {

    val routers: Routers
}

open class Routers {

    protected val _routers = mutableMapOf<String, IRouter>()

    val size: Int
        get() {
            return _routers.size
        }

    fun register(obj: IRouter) {
        if (_routers.get(obj.action) != null) {
            logger.fatal("已经注册了一个同名的路由${obj.action}")
            return
        }
        _routers[obj.action] = obj
    }

    fun find(act: String): IRouter? {
        return _routers[act]
    }

    fun unregister(act: String) {
        _routers.remove(act)
    }

    fun forEach(proc: (v: IRouter, k: String) -> Unit) {
        _routers.forEach {
            proc(it.value, it.key)
        }
    }

    fun toArray(): Collection<IRouter> {
        return _routers.values
    }

    suspend fun process(trans: Transaction) {
        val ac = trans.ace

        // 开始事务处理流程
        trans.begin()

        // 查找router
        val r = _routers[trans.router]
        if (r == null) {
            trans.status = STATUS.ROUTER_NOT_FOUND
            trans.message = "router not found"
            trans.submit()
            return
        }

        // 模型化
        val sta = trans.modelize(r)
        if (sta != STATUS.OK) {
            trans.status = sta
            trans.message = "modelize failed"
            trans.submit()
            return
        }

        // 恢复数据上下文
        trans.collect()

        // 检查是否需要验证
        if (ac != null && ac.ignore) {
            // 不做权限判断
        } else if (!trans.expose) {
            // 访问权限判断
            if (trans.needAuth()) {
                if (!trans.auth()) {
                    trans.status = STATUS.NEED_AUTH
                    trans.message = "need auth"
                    trans.submit()
                    return
                }
            } else {
                // 检查devops
                if (!devopscheck(trans)) {
                    trans.status = STATUS.PERMISSION_FAILED
                    trans.message = "permission failed"
                    trans.submit()
                    return
                }
            }
        }

        // 调用处理
        val ap = FindAction(r, trans.call)
        if (ap == null) {
            trans.status = STATUS.ACTION_NOT_FOUND
            trans.message = "action not found"
            trans.submit()
            return
        }

        // 不论同步或者异步模式，默认认为是成功的，业务逻辑如果出错则再次设置status为对应的错误码        
        trans.status = STATUS.OK
        try {
            if (ap.func.isSuspend)
                ap.func.callSuspend(r, trans)
            else
                ap.func.call(r, trans)
        } catch (err: Throwable) {
            if (err is ModelError) {
                trans.status = ToEnum(STATUS::class, err.code, STATUS.EXCEPTION)!!
                trans.message = err.message
            } else if (err is InvocationTargetException) {
                trans.status = STATUS.EXCEPTION
                trans.message = err.targetException.localizedMessage
            } else {
                trans.status = STATUS.EXCEPTION
                trans.message = err.message ?: err.localizedMessage
            }
            trans.submit()
        }
    }

    // devops下的权限判断
    protected open suspend fun devopscheck(trans: Transaction): Boolean {
        return true
    }
}