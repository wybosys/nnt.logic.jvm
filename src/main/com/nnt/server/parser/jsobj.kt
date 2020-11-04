package com.nnt.server.parser

import com.nnt.core.FieldOption
import com.nnt.core.STATUS
import kotlin.reflect.KClass

class Jsobj : AbstractParser() {

    override fun checkInput(proto: KClass<*>, params: Map<String, *>): STATUS {
        TODO("Not yet implemented")
    }

    override fun decodeField(fp: FieldOption, value: Any, input: Boolean, output: Boolean): Any {
        TODO("Not yet implemented")
    }

    override fun fill(mdl: Any, params: Map<String, *>, input: Boolean, output: Boolean) {
        TODO("Not yet implemented")
    }

}