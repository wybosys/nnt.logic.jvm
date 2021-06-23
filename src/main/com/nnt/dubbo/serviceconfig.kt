package com.nnt.dubbo

import org.apache.dubbo.config.ServiceConfig

class ServiceConfig : ServiceConfig<Any>() {

    lateinit var serviceClass: Class<*>

    init {
        version = "1.0.0"
        protocols = mutableListOf()
    }

    override fun getServiceClass(ref: Any?): Class<*> {
        return serviceClass
    }
}