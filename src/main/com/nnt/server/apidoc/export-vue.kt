package com.nnt.server.apidoc

open class ExportVue : ExportNode() {

    init {
        template = "bundle://nnt/server/apidoc/apis-vue.dust"
    }
}
