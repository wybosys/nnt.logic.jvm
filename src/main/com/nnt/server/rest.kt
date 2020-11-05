package com.nnt.server

import com.nnt.core.IRouter
import com.nnt.core.JsonObject
import com.nnt.core.Seconds
import com.nnt.core.logger
import com.nnt.manager.App
import com.nnt.manager.Config
import com.nnt.server.rest.ParseContentToParams
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

open class Rest : AbstractServer(), IRouterable, IConsoleServer, IApiServer, IHttpServer {

    var listen: String = ""
    var port: Int = 80
    var timeout: Seconds = 0.0

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("listen")) {
            logger.fatal("${id} 没有配置服务器地址")
            return false
        }
        val th = cfg["listen"]!!.asString()
        val sp = th.split(":")
        if (sp.size == 1) {
            listen = th
        } else {
            listen = sp[0]
            port = sp[1].toInt()
        }
        if (listen == "*")
            listen = "0.0.0.0"

        if (cfg.has("timeout"))
            timeout = cfg["timeout"]!!.asDecimal()

        // 读取配置文件中配置的router
        if (cfg.has("router")) {
            val router = cfg["router"]!!
            if (router.isArray) {
                for (v in router.asArray()) {
                    val t = App.shared.instanceEntry(v.asString())
                    if (t == null) {
                        logger.fatal("没有找到该类型 ${v.asString()}")
                        return false
                    } else {
                        _routers.register(t as IRouter)
                    }
                }
            } else if (router.isMap) {
                for ((key, value) in router.asMap()) {
                    val t = App.shared.instanceEntry(key as String)
                    if (t == null) {
                        logger.fatal("没有找到该类型 ${key}")
                        return false
                    }
                    val tr = t as IRouter
                    if (!tr.config(value)) {
                        logger.fatal("类型 ${key} 配置失败")
                        return false
                    }
                    _routers.register(tr)
                }
            }
        }

        return true
    }

    private lateinit var _svc: RestVerticle
    private val _env = Vertx.vertx()

    override fun start() {
        _svc = RestVerticle(this, _env)
        _env.deployVerticle(_svc)
    }

    override fun stop() {
        // 启动后，svc会具有形如uuid的did
        // println(_svc.deploymentID())
        _env.undeploy(_svc.deploymentID())
    }

    private val _routers = Routers()

    override val routers: Routers = _routers

    // 处理请求
    override fun invoke(params: Properties, req: Any, rsp: Any) {
        TODO("Not yet implemented")
    }


    override var imgsrv: String = ""
    override var mediasrv: String = ""

    override fun httpserver(): Any {
        return _svc
    }

    protected open fun instanceTransaction(): Transaction {
        return EmptyTransaction()
    }
}

private class RestVerticle(val rest: Rest, val env: Vertx) : AbstractVerticle() {

    override fun start() {
        val opts = HttpServerOptions()
        opts.logActivity = Config.DEBUG

        val srv = env.createHttpServer(opts)
        val router = Router.router(env).apply {
            get("/").handler { hdl ->
                val req = hdl.request()
                req.bodyHandler { body ->
                    GlobalScope.launch {
                        doWorker(hdl, body)
                    }
                }
            }
            post("/").handler { hdl ->
                val req = hdl.request()

                // 只针对form/urlencoded打开自动参数处理
                val ct = req.getHeader("content-type")
                req.isExpectMultipart = ct.indexOf("form") != -1

                req.bodyHandler { body ->
                    GlobalScope.launch {
                        doWorker(hdl, body)
                    }
                }
            }
        }

        srv.requestHandler {
            router.handle(it)
        }

        srv.listen(rest.port, rest.listen) {
            if (it.succeeded()) {
                logger.info("创建 ${rest.id}@rest")
            } else {
                logger.info("创建 ${rest.id}@rest 失败")
            }
        }
    }

    suspend fun doWorker(hdl: RoutingContext, body: Buffer) {
        val req = hdl.request()
        val rsp = hdl.response()

        // 打开跨域支持
        rsp.putHeader("Access-Control-Allow-Origin", "*")
        rsp.putHeader("Access-Control-Allow-Credentials", "true")
        rsp.putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")

        // 直接对option进行成功响应
        if (req.method() == HttpMethod.OPTIONS) {
            val t = req.getHeader("access-control-request-headers")
            if (t.isNotEmpty()) {
                rsp.putHeader("Access-Control-Allow-Headers", t)
                if (req.getHeader("access-control-request-method") == "POST") {
                    rsp.putHeader("Content-Type", "multipart/form-data")
                }
            }
            rsp.statusCode = 204
            rsp.end()
            return
        }

        // 处理url请求
        val url = req.uri()
        val path = req.path()
        logger.log(url)

        // 请求数据类型
        val ct = req.getHeader("content-type").split(";")[0]

        // 支持几种不同的路由格式
        // ?action=xxx.yyy&params
        // $$/xxx.yyy$params
        // xxx/yyy&params
        val params = mutableMapOf<String, Any?>()
        req.params().forEach {
            params[it.key] = it.value
        }

        // 为了支持第三方平台通过action同名传递动作
        if (path.startsWith("/$$/") || path.startsWith("/action/")) {
            val p = path.split("/")
            for (i in 0..p.size step 2) {
                val k = p[i]
                val v = p[i + 1]
                params[k] = v
            }
        } else {
            val p = path.split("/").filter {
                it.isNotEmpty()
            }
            if (p.size >= 2) {
                val r = p[p.size - 2]
                val a = p[p.size - 1]
                params["action"] = "${r}.${a}"
            }
        }

        // 如果是post请求，则处理一下form数据
        if (req.method() == HttpMethod.POST) {
            // 处理form-data
            req.formAttributes().forEach {
                params[it.key] = it.value
            }

            // 如果是multipart-form得请求，则不适用于处理buffer
            ParseContentToParams(body, ct).forEach {
                params[it.key] = it.value
            }

            // 处理文件
            hdl.fileUploads().forEach {
                params[it.name()] = it
            }
        }
    }

}