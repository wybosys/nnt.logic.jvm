package com.nnt.flink

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode

fun JsonNode.toText(def: String? = null): String? {
    return if (isNull) return def else asText(def ?: "")
}

fun JsonNode.toInt(def: Int? = null): Int? {
    return if (isNull) return def else asInt(def ?: 0)
}

fun JsonNode.toLong(def: Long? = null): Long? {
    return if (isNull) return def else asLong(def ?: 0L)
}

fun JsonNode.toBoolean(def: Boolean? = null): Boolean? {
    return if (isNull) return def else asBoolean(def ?: false)
}
