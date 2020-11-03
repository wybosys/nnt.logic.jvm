package com.nnt.server

interface IApiServer {

    // 用来给api提供图床，设置的是对应服务的srvid
    var imgsrv: String

    // 用来给api提供视频、音频池
    var mediasrv: String
}

interface IHttpServer {

    // 返回内部实现的http原始句柄 http.Server
    fun httpserver(): Any
    
}
