package com.nnt.server.apidoc

import com.nnt.core.ActionProto

open class ExportH5G : ExportNode() {

    init {
        template = "bundle://nnt/server/apidoc/apis-h5g.dust"
    }

    override fun actionType(ap: ActionProto): String {
        val nm = ap.clazz.simpleName!!
        return "models.$nm"
    }
}
