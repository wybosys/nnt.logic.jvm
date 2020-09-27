package com.nnt.dubbo

import org.apache.dubbo.config.ServiceConfig

class ServiceConfig : ServiceConfig<Any>() {

    lateinit var serviceClass: Class<*>

    override fun getServiceClass(ref: Any?): Class<*> {
        return serviceClass
    }
}