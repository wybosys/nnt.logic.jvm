package com.nnt.server

import com.nnt.core.JsonObject
import com.nnt.core.logger
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler

val DEFAULT_PORT = 80

open class WebPage : AbstractServer() {

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

        if (cfg.has("index"))
            index = cfg["index"]!!.asString()

        if (!cfg.has("root")) {
            logger.fatal("${id} 没有定义root目录")
            return false
        }
        root = cfg["root"]!!.asString()

        if (cfg.has("path"))
            path = cfg["path"]!!.asString()

        return true
    }

    var listen: String = ""
    var port: Int = DEFAULT_PORT
    var root: String = ""
    var index: String = "index.html"
    var path: String = ""

    private val _env = Vertx.vertx()
    private lateinit var _srv: HttpServer

    override fun start() {
        _srv = _env.createHttpServer()
        val r = Router.router(_env)
        val hdl = StaticHandler.create(root)

        if (path.isNotEmpty()) {
            r.route("/").handler(hdl)
            r.route("$path/*").handler(hdl)
        } else {
            r.route("/*").handler(hdl)
        }

        @Suppress("DEPRECATION")
        _srv.requestHandler(r::accept).listen(port, listen)

        logger.info("启动 ${id}@web")
        onStart()
    }

    override fun stop() {
        _srv.close()
    }

}