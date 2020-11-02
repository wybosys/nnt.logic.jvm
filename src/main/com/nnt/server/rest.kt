package com.nnt.server

import com.nnt.core.Jsonobj
import com.nnt.core.logger
import com.nnt.manager.Config
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router

open class Rest : AbstractServer() {

    var host: String = ""
    var port: Int = 80

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("host")) {
            logger.fatal("${id} 没有配置数据库地址")
            return false
        }
        val th = cfg["host"].asText()
        val sp = th.split(":")
        if (sp.size == 1) {
            host = th
        } else {
            host = sp[0]
            port = sp[1].toInt()
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
        _svc.stop()
    }

}

private class RestVerticle(val rest: Rest, val env: Vertx) : AbstractVerticle() {

    override fun start() {
        val opts = HttpServerOptions()
        opts.logActivity = Config.DEBUG

        val srv = env.createHttpServer(opts)
        val router = Router.router(env).apply {
            get("/").handler(Handler {
                
            })
        }

        srv.requestHandler {
            router.handle(it)
        }

        srv.listen(rest.port, rest.host) {
            if (it.succeeded()) {
                logger.info("创建 ${rest.id}@rest")
            } else {
                logger.info("创建 ${rest.id}@rest 失败")
            }
        }
    }

}