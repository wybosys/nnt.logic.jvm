package com.nnt.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

typealias Jsonobj = JsonNode

fun toJsonObject(str: String?): Jsonobj? {
    if (str == null)
        return null
    val mapper = ObjectMapper()
    return mapper.readTree(str)
}

fun toJsonObject(obj: Jsonobj?): Jsonobj? {
    return obj
}

fun toJson(jobj: Jsonobj?): String {
    if (jobj == null)
        return ""
    val mapper = ObjectMapper()
    return mapper.writeValueAsString(jobj)
}

fun Merge(base: Jsonobj, r: Jsonobj?): Jsonobj {
    if (r == null || !base.isObject || !r.isObject)
        return base
    (base as ObjectNode).setAll<ObjectNode>(r as ObjectNode)
    return base
}