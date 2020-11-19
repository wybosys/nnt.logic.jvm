package com.nnt.server.apidoc

import com.nnt.core.ActionProto
import com.nnt.core.FieldOption
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

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
        if (typ.isSubclassOf(String::class)) {
            return "string"
        }
        if (typ.isSubclassOf(Number::class)) {
            return "number"
        }
        if (typ.isSubclassOf(Boolean::class)) {
            return "boolean"
        }
        return typ.simpleName ?: "undecl"
    }

    override fun type(fp: FieldOption): String {
        if (fp.string)
            return "string"
        if (fp.integer || fp.decimal)
            return "number"
        if (fp.boolean)
            return "boolean"
        if (fp.array)
            return "Array<" + type(fp.valtype!!) + ">"
        if (fp.map)
            return "Map<" + type(fp.keytype!!) + ", " + type(fp.valtype!!) + ">"
        if (fp.json)
            return "Object"
        return fp.valtype?.simpleName ?: "undecl"
    }

    override fun fieldType(typ: KClass<*>): String {
        if (typ.isSubclassOf(String::class)) {
            return "Model.string_t"
        }
        if (typ.isSubclassOf(Int::class) || typ.isSubclassOf(Long::class) || typ.isSubclassOf(Short::class)) {
            return "Model.integer_t"
        }
        if (typ.isSubclassOf(Float::class) || typ.isSubclassOf(Double::class)) {
            return "Model.double_t"
        }
        if (typ.isSubclassOf(Boolean::class)) {
            return "Model.boolean_t"
        }
        return typ.simpleName ?: "undecl"
    }

    override fun fieldTypes(fp: FieldOption): String {
        val t = mutableListOf<String>()
        if (fp.keytype != null) {
            t.add(fieldType(fp.keytype!!))
        }
        if (fp.valtype != null) {
            t.add(fieldType(fp.valtype!!))
        }
        return t.joinToString(", ")
    }

    override fun fieldOptions(fp: FieldOption): String {
        val r = mutableListOf<String>()
        if (fp.input)
            r.add("Model.input")
        if (fp.output)
            r.add("Model.output")
        if (fp.optional)
            r.add("Model.optional")
        return "[" + r.joinToString(", ") + "]"
    }

    override fun fieldComment(fp: FieldOption): String {
        return if (fp.comment.isEmpty()) "" else """, "${fp.comment}""""
    }

    override fun field(fp: FieldOption): String {
        var deco: String
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