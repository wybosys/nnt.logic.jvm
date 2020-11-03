package com.nnt.core

import com.nnt.component.TodayInit

fun Init() {

    // 注册url请求
    RegisterScheme("http") { }
    RegisterScheme("https") { }

    // 注册处理bundle资源
    RegisterScheme("bundle") {
        if (!IsJar()) {
            it.path = "${HOME}/../src/main/resources/${it.path}"
        } else {
            it.bundle = true
        }
    }

    // 初始化今日日期工具
    TodayInit()
}