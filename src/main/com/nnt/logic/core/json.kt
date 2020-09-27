package com.nnt.logic.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

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