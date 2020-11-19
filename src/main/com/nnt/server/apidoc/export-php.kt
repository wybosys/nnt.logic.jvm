package com.nnt.server.apidoc

import com.nnt.core.ActionProto
import com.nnt.core.FieldOption
import kotlin.reflect.KClass

open class ExportPHP : Export() {

    init {
        template = "bundle://nnt/server/apidoc/apis-php.dust"
        params.filename += ".php"

        // 处理名域
        val sp = params.domain.split('/')
        params.namespace = sp[0].capitalize() + "\\" + sp[1].capitalize()
    }

    override suspend fun render(): String {
        val out = super.render()
        return "<?php\n$out"
    }

    override fun type(typ: KClass<*>): String {
        TODO("Not yet implemented")
    }

    override fun type(fp: FieldOption): String {
        TODO("Not yet implemented")
    }

    override fun fieldType(typ: KClass<*>): String {
        TODO("Not yet implemented")
    }

    override fun fieldTypes(fp: FieldOption): String {
        TODO("Not yet implemented")
    }

    override fun fieldOptions(fp: FieldOption): String {
        TODO("Not yet implemented")
    }

    override fun fieldComment(fp: FieldOption): String {
        TODO("Not yet implemented")
    }

    override fun field(fp: FieldOption): String {
        var deco = ""
        if (fp.string) {
            deco = "@Api(" + fp.id + ", [string], " + fieldOptions(fp) + fieldComment(fp) + ")";
            deco += "\n\t* @var string"
        } else if (fp.integer) {
            deco = "@Api(" + fp.id + ", [integer], " + fieldOptions(fp) + fieldComment(fp) + ")";
            deco += "\n\t* @var int"
        } else if (fp.decimal) {
            deco = "@Api(" + fp.id + ", [double], " + fieldOptions(fp) + fieldComment(fp) + ")";
            deco += "\n\t* @var double"
        } else if (fp.boolean) {
            deco = "@Api(" + fp.id + ", [boolean], " + fieldOptions(fp) + fieldComment(fp) + ")";
            deco += "\n\t* @var boolean"
        } else if (fp.array) {
            deco =
                "@Api(" + fp.id + ", [array, " + fieldTypes(fp) + "], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.map) {
            deco =
                "@Api(" + fp.id + ", [map, " + fieldTypes(fp) + "], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.enum) {
            deco =
                "@Api(" + fp.id + ", [enum, " + fieldTypes(fp) + "], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.file) {
            deco = "@Api(" + fp.id + ", [file], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.filter) {
            deco = "@Api(" + fp.id + ", [filter], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.json) {
            deco = "@Api(" + fp.id + ", [json], " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else {
            deco =
                "@Api(" + fp.id + ", [type, " + fieldTypes(fp) + "], " + fieldOptions(fp) + fieldComment(fp) + ")"
        }
        return deco;
    }

    override fun actionType(ap: ActionProto): String {
        val nm = ap.clazz.simpleName!!
        return "M$nm"
    }
}