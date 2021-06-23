package com.nnt.server

import com.nnt.core.JsonObject
import com.nnt.core.Socket
import com.nnt.core.logger
import com.nnt.manager.App
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpcweb.GrpcPortNumRelay
import io.grpcweb.JettyWebserverForGrpcwebTraffic

open class GrpcWeb : AbstractServer() {

    var port: Int = 80
    var grpcport: Int = Socket.RandomPort()
    private var _binded = false // 是否是绑定模式

    val _svcs = mutableListOf<BindableService>()

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
            // 当前grpc-web不支持跨服务器监听
        } else {
            port = sp[1].toInt()
        }

        // 配置为绑定
        @Suppress("NAME_SHADOWING")
        if (cfg.has("bindto")) {
            val th = cfg["bindto"]!!.asString()
            val sp = th.split(":")
            if (sp.size == 1) {
                // 当前grpc-web不支持跨服务器监听
            } else {
                grpcport = sp[1].toInt()
            }
            _binded = true
        } else if (cfg.has("service")) {
            // 配置为独立服务
            val service = cfg["service"]!!
            if (service.isArray) {
                for (v in service.asArray()) {
                    val t = App.shared.instanceEntry(v.asString())
                    if (t == null) {
                        logger.fatal("没有找到该类型 ${v.asString()}")
                        return false
                    }
                    _svcs.add(t as BindableService)
                }
            }
        }

        return true
    }

    private var _svr_grpc: Server? = null
    private var _svr_web: org.eclipse.jetty.server.Server? = null

    override fun start() {
        if (!_binded) {
            // logger.log("grpc端口为 ${grpcport}")

            // 启动内置grpc服务
            val sb = ServerBuilder.forPort(grpcport)

            _svcs.forEach {
                sb.addService(it)
            }
            _svr_grpc = sb.build()
            _svr_grpc!!.start()
        }

        // 启动web服务
        _svr_web = JettyWebserverForGrpcwebTraffic(port).start()
        GrpcPortNumRelay.setGrpcPortNum(grpcport)

        logger.info("启动 ${id}@grpc-web")
    }

    override fun stop() {
        _svr_web?.stop()
        _svr_web = null

        _svr_grpc?.shutdownNow()
        _svr_grpc = null
    }
}