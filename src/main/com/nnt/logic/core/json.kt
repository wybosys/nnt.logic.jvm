package com.nnt.logic.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

typealias JsonObject = JsonNode

fun toJsonObject(str: String?): JsonObject? {
    if (str == null)
        return null
    val mapper = ObjectMapper()
    return mapper.readTree(str)
}

fun toJsonObject(obj: JsonObject?): JsonObject? {
    return obj
}
