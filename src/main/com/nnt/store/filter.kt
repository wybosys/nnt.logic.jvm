package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.toJson
import com.nnt.core.toJsonObject

/**
 * {
 *     "and": [
 *         {"abc": { "gt": 100 }}
 *     ]
 * }
 */

val KEYWORDS = setOf("and", "or")
val OPERATORS = setOf("gt", "gte", "eq", "not", "lt", "lte", "search")

class Filter {

    fun clear() {
        ands.clear()
        ors.clear()
        key = null
        value = null
    }

    var ands = mutableListOf<Filter>()
    var ors = mutableListOf<Filter>()
    var key: String? = null
    var operator: String? = null
    var value: JsonObject? = null

    // 解析，如果解析全新，需要手动调用clear方法
    fun parse(jsobj: JsonObject): Boolean {
        for ((k, v) in jsobj.asMap()) {
            if (k == "and") {
                if (v.isArray) {
                    for (e in v.asArray()) {
                        val sub = Filter()
                        if (sub.parse(e)) {
                            ands.add(sub)
                        } else {
                            return false
                        }
                    }
                } else {
                    return false
                }
            } else if (k == "or") {
                if (v.isArray) {
                    for (e in v.asArray()) {
                        val sub = Filter()
                        if (sub.parse(e)) {
                            ors.add(sub)
                        } else {
                            return false
                        }
                    }
                } else {
                    return false
                }
            } else if (OPERATORS.contains(k)) {
                operator = k as String
                value = v
            } else {
                key = k as String
                for ((sk, sv) in v.asMap()) {
                    val sub = Filter()
                    sub.operator = sk as String
                    sub.value = sv
                    ands.add(sub)
                }
            }
        }
        return true
    }

    protected fun attachToJsobj(obj: FilterPOD) {
        if (key == null) {
            if (ands.size > 0) {
                val and: FilterNodePOD
                if (!obj.contains("and")) {
                    and = mutableListOf()
                    obj["and"] = and
                } else {
                    @Suppress("UNCHECKED_CAST")
                    and = obj["and"] as FilterNodePOD
                }

                ands.forEach { e ->
                    val ref: FilterPOD = mutableMapOf()
                    and.add(ref)

                    e.attachToJsobj(ref)
                }
            }

            if (ors.size > 0) {
                val or: FilterNodePOD
                if (!obj.contains("or")) {
                    or = mutableListOf()
                    obj["or"] = or
                } else {
                    @Suppress("UNCHECKED_CAST")
                    or = obj["or"] as FilterNodePOD
                }

                ors.forEach { e ->
                    val ref: FilterPOD = mutableMapOf()
                    or.add(ref)

                    e.attachToJsobj(ref)
                }
            }

            if (operator != null && value != null) {
                obj[operator!!] = value!!
            }
        }

        if (key != null) {
            val ref: FilterNodePOD = mutableListOf()
            obj[key as String] = ref

            if (ands.size > 0) {
                val and: FilterNodePOD
                if (!obj.contains("and")) {
                    and = mutableListOf()
                    obj["and"] = and
                } else {
                    @Suppress("UNCHECKED_CAST")
                    and = obj["and"] as FilterNodePOD
                }

                ands.forEach { e ->
                    val t: FilterPOD = mutableMapOf()
                    and.add(t)

                    e.attachToJsobj(t)
                }
            }

            if (ors.size > 0) {
                val or: FilterNodePOD
                if (!obj.contains("or")) {
                    or = mutableListOf()
                    obj["or"] = or
                } else {
                    @Suppress("UNCHECKED_CAST")
                    or = obj["or"] as FilterNodePOD
                }

                ors.forEach { e ->
                    val t: FilterPOD = mutableMapOf()
                    or.add(t)

                    e.attachToJsobj(t)
                }
            }
        }
    }

    override fun toString(): String {
        val r: FilterPOD = mutableMapOf()
        attachToJsobj(r)
        return toJson(r)
    }

    companion object {

        fun Parse(str: String): Filter? {
            val jsobj = toJsonObject(str)
            if (jsobj == null || jsobj.isNull) {
                return null
            }
            val r = Filter()
            if (!r.parse(jsobj)) {
                return null
            }
            return r
        }
    }
}

private typealias FilterPOD = MutableMap<String, Any>
private typealias FilterNodePOD = MutableList<FilterPOD>