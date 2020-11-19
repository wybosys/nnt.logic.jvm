package com.nnt.server.apidoc

import com.nnt.core.ActionProto
import com.nnt.core.FieldOption
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

open class ExportNode : Export() {

    init {
        template = "bundle://nnt/server/apidoc/apis-node.dust"
        params.filename += ".ts"
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
        if (fp.intfloat != null)
            return "number"
        if (fp.timestamp)
            return "number"
        if (fp.filter)
            return "any"
        return fp.valtype?.simpleName ?: "undecl"
    }

    override fun fieldTypes(fp: FieldOption): String {
        val t = mutableListOf<String>()
        if (fp.keytype != null) {
            t.add(fieldType(fp.keytype!!))
        }
        if (fp.valtype != null) {
            t.add(fieldType(fp.valtype!!))
        }
        if (fp.intfloat != null) {
            t.add(fp.intfloat!!.toString())
        }
        return t.joinToString(", ")
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
            deco = "@Model.string(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.integer) {
            deco = "@Model.integer(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")";
        } else if (fp.decimal) {
            deco = "@Model.double(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.intfloat != null) {
            deco = "@Model.intfloat(" + fp.id + ", " + fieldTypes(fp) + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.boolean) {
            deco = "@Model.boolean(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.array) {
            deco = "@Model.array(" + fp.id + ", " + fieldTypes(fp) + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.map) {
            deco = "@Model.map(" + fp.id + ", " + fieldTypes(fp) + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.enum) {
            deco =
                "@Model.enumerate(" + fp.id + ", " + fieldTypes(fp) + ", " + fieldOptions(fp) + fieldComment(fp) + ")";
        } else if (fp.file) {
            deco = "@Model.file(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.filter) {
            deco = "@Model.filter(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.json) {
            deco = "@Model.json(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        } else if (fp.timestamp) {
            deco = "@Model.integer(" + fp.id + ", " + fieldOptions(fp) + fieldComment(fp) + ")";
        } else {
            deco = "@Model.type(" + fp.id + ", " + fieldTypes(fp) + ", " + fieldOptions(fp) + fieldComment(fp) + ")"
        }
        return deco
    }

    override fun actionType(ap: ActionProto): String {
        return ap.clazz.simpleName!!
    }
}