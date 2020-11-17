package com.nnt.server.apidoc

import com.nnt.core.FieldOption
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun FpToTypeDef(fp: FieldOption): String {
    var typ = ""
    if (fp.string) {
        typ = "string"
    } else if (fp.integer) {
        typ = "number"
    } else if (fp.decimal) {
        typ = "number"
    } else if (fp.boolean) {
        typ = "boolean"
    } else if (fp.intfloat != null) {
        typ = "number";
    } else if (fp.array) {
        typ = "Array<" + ValtypeDefToDef(fp.valtype!!) + ">"
    } else if (fp.map) {
        typ = "Map<" + ValtypeDefToDef(fp.keytype!!) + ", " + ValtypeDefToDef(fp.valtype!!) + ">"
    } else if (fp.enum) {
        typ = fp.valtype!!.simpleName!!
    } else if (fp.file) {
        if (fp.input)
            typ = "any"
        else
            typ = "string"
    } else if (fp.filter) {
        typ = "string"
    } else if (fp.json) {
        typ = "Object"
    } else {
        typ = fp.valtype!!.simpleName!!
    }
    return typ
}

fun FpToOptionsDef(fp: FieldOption, ns: String = ""): String {
    val r = mutableListOf<String>()
    if (fp.input)
        r.add(ns + "input")
    if (fp.output)
        r.add(ns + "output")
    if (fp.optional)
        r.add(ns + "optional")
    return "[" + r.joinToString(", ") + "]"
}

fun FpToValtypeDef(fp: FieldOption, ns: String = ""): String {
    val t = mutableListOf<String>()
    if (fp.keytype != null) {
        t.add(ValtypeDefToDef(fp.keytype!!, ns))
    }
    if (fp.valtype != null) {
        t.add(ValtypeDefToDef(fp.valtype!!, ns))
    }
    if (fp.intfloat != null) {
        t.add("${ns}number")
    }
    return t.joinToString(", ");
}

fun ValtypeDefToDef(def: KClass<*>, ns: String = ""): String {
    if (def.isSubclassOf(String::class)) {
        return "${ns}string"
    }
    if (def.isSubclassOf(Number::class)) {
        return "${ns}number"
    }
    if (def.isSubclassOf(Boolean::class)) {
        return "${ns}boolean"
    }
    return def.simpleName ?: "undecl"
}

fun FpToCommentDef(fp: FieldOption): String {
    return if (fp.comment.isEmpty()) "" else """, "${fp.comment}""""
}

fun FpToDecoDef(fp: FieldOption, ns: String = ""): String {
    var deco = ""
    if (fp.string) {
        deco = "@" + ns + "string(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else if (fp.integer) {
        deco = "@" + ns + "integer(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")";
    } else if (fp.decimal) {
        deco = "@" + ns + "double(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else if (fp.intfloat != null) {
        deco = "@" + ns + "intfloat(" + fp.id + ", " + FpToValtypeDef(fp, ns) + ", " + FpToOptionsDef(fp,
            ns) + FpToCommentDef(fp) + ")"
    } else if (fp.boolean) {
        deco = "@" + ns + "boolean(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else if (fp.array) {
        deco = "@" + ns + "array(" + fp.id + ", " + FpToValtypeDef(fp, ns) + ", " + FpToOptionsDef(fp,
            ns) + FpToCommentDef(fp) + ")"
    } else if (fp.map) {
        deco =
            "@" + ns + "map(" + fp.id + ", " + FpToValtypeDef(fp, ns) + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(
                fp) + ")"
    } else if (fp.enum) {
        deco = "@" + ns + "enumerate(" + fp.id + ", " + FpToValtypeDef(fp, ns) + ", " + FpToOptionsDef(fp,
            ns) + FpToCommentDef(fp) + ")";
    } else if (fp.file) {
        deco = "@" + ns + "file(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else if (fp.filter) {
        deco = "@" + ns + "filter(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else if (fp.json) {
        deco = "@" + ns + "json(" + fp.id + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(fp) + ")"
    } else {
        deco =
            "@" + ns + "type(" + fp.id + ", " + FpToValtypeDef(fp, ns) + ", " + FpToOptionsDef(fp, ns) + FpToCommentDef(
                fp) + ")"
    }
    return deco
}

fun FpToDecoDefPHP(fp: FieldOption): String {
    var deco = ""
    if (fp.string) {
        deco = "@Api(" + fp.id + ", [string], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")";
        deco += "\n\t* @var string"
    } else if (fp.integer) {
        deco = "@Api(" + fp.id + ", [integer], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")";
        deco += "\n\t* @var int"
    } else if (fp.decimal) {
        deco = "@Api(" + fp.id + ", [double], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")";
        deco += "\n\t* @var double"
    } else if (fp.boolean) {
        deco = "@Api(" + fp.id + ", [boolean], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")";
        deco += "\n\t* @var boolean"
    } else if (fp.array) {
        deco =
            "@Api(" + fp.id + ", [array, " + FpToValtypeDef(fp) + "], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else if (fp.map) {
        deco =
            "@Api(" + fp.id + ", [map, " + FpToValtypeDef(fp) + "], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else if (fp.enum) {
        deco =
            "@Api(" + fp.id + ", [enum, " + FpToValtypeDef(fp) + "], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else if (fp.file) {
        deco = "@Api(" + fp.id + ", [file], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else if (fp.filter) {
        deco = "@Api(" + fp.id + ", [filter], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else if (fp.json) {
        deco = "@Api(" + fp.id + ", [json], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    } else {
        deco =
            "@Api(" + fp.id + ", [type, " + FpToValtypeDef(fp) + "], " + FpToOptionsDef(fp) + FpToCommentDef(fp) + ")"
    }
    return deco;
}
