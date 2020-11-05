package com.nnt.server

import com.nnt.server.parser.RegisterParser
import com.nnt.server.render.RegisterRender

fun Init() {

    // 注册支持的输出格式
    RegisterRender("json", com.nnt.server.render.Json())
    RegisterRender("raw", com.nnt.server.render.Raw())
    //RegisterRender("bin", com.nnt.server.render.Bin())

    // 注册支持的输入格式
    RegisterParser("jsobj", com.nnt.server.parser.Jsobj())
    //RegisterParser("bin", com.nnt.server.parser.Bin())

}
