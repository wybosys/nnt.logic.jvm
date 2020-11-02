package com.nnt.server

import com.nnt.core.IRouter
import com.nnt.core.logger

interface IRouterable {

    val routers: Routers
}

class Routers {

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

    }
}