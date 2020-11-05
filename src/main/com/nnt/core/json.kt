package com.nnt.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

typealias JsonMap = MutableMap<Any, Any?>
typealias JsonArray = MutableList<Any?>
typealias JsonInteger = Long
typealias JsonReal = Double
typealias JsonBoolean = Boolean

class JsonMapType : TypeReference<JsonMap>() {}
class JsonArrayType : TypeReference<JsonArray>() {}

enum class JsonType {
    MAP,
    ARRAY,
    POD,
    NULL
}

class JsonObject {

    val size: Int
        get() {
            if (_map != null)
                return _map!!.size
            if (_arr != null)
                return _arr!!.size
            return 0
        }

    val isMap: Boolean
        get() {
            return _map != null
        }

    val isArray: Boolean
        get() {
            return _arr != null
        }

    val isNull: Boolean
        get() {
            return _map == null && _arr == null && _pod == null
        }

    fun clear() {
        _map = null
        _arr = null
        _pod = null
    }

    val type: JsonType
        get() {
            if (_map != null)
                return JsonType.MAP
            if (_arr != null)
                return JsonType.ARRAY
            if (_pod != null)
                return JsonType.POD
            return JsonType.NULL
        }

    private var _map: MutableMap<Any, JsonObject>? = null
    private var _arr: MutableList<JsonObject>? = null
    private var _pod: Any? = null

    operator fun get(kid: Any): JsonObject? {
        if (_map != null) {
            return _map!![kid]
        } else if (_arr != null) {
            return _arr!![kid as Int]
        }
        return null
    }

    operator fun <V> set(key: Any, v: V?): JsonObject {
        if (_map != null) {
            val t = JsonObject()
            t._pod = v
            _map!![key] = t
        }
        return this
    }

    fun has(kv: Any): Boolean {
        if (_map != null)
            return _map!!.containsKey(kv)
        if (_arr != null)
            return _arr!!.contains(kv)
        return _pod == kv
    }

    fun <V> add(v: V?): JsonObject {
        if (_arr != null) {
            val t = JsonObject()
            t._pod = v
            _arr!!.add(t)
        }
        return this
    }

    fun from(str: String?): JsonObject {
        if (str == null)
            return this
        return from(ObjectMapper().readTree(str))
    }

    fun from(node: JsonNode?): JsonObject {
        if (node == null || node.isNull)
            return this

        if (node.isArray) {
            _arr = mutableListOf()
            node.forEach {
                _arr!!.add(JsonObject().from(it))
            }
        } else if (node.isObject) {
            _map = mutableMapOf()
            node.fields().forEach {
                _map!![it.key] = JsonObject().from(it.value)
            }
        } else if (node.isTextual) {
            _pod = node.asText()
        } else if (node.isLong || node.isInt || node.isShort) {
            _pod = node.asLong()
        } else if (node.isDouble || node.isFloat) {
            _pod = node.asDouble()
        } else if (node.isBoolean) {
            _pod = node.asBoolean()
        } else {
            logger.fatal("json解析遇到不支持的类型")
        }

        return this
    }

    protected fun from_map(map: Map<*, *>): JsonObject {
        _map = mutableMapOf()
        map.forEach {
            _map!![it.key as Any] = JsonObject().from_any(it.value)
        }
        return this
    }

    protected fun from_list(lst: List<*>): JsonObject {
        _arr = mutableListOf()
        lst.forEach {
            _arr!!.add(JsonObject().from_any(it))
        }
        return this
    }

    protected fun from_list(lst: Array<*>): JsonObject {
        _arr = mutableListOf()
        lst.forEach {
            _arr!!.add(JsonObject().from_any(it))
        }
        return this
    }

    protected fun <V> from_pod(v: V): JsonObject {
        _pod = v
        return this
    }

    protected fun from_any(v: Any?): JsonObject {
        if (v == null)
            return this
        if (v is Map<*, *>)
            return from_map(v)
        if (v is List<*>)
            return from_list(v)
        return from_pod(v)
    }

    override fun toString(): String {
        if (isNull)
            return ""

        // 转换成普通类型
        val t = flat()
        return ObjectMapper().writeValueAsString(t)
    }

    fun flat(): Any? {
        if (_map != null) {
            val r = mutableMapOf<Any, Any?>()
            _map!!.forEach {
                r[it.key] = it.value.flat()
            }
            return r
        }
        if (_arr != null) {
            val r = mutableListOf<Any?>()
            _arr!!.forEach {
                r.add(it.flat())
            }
            return r
        }
        return _pod
    }

    fun merge(r: JsonObject?): JsonObject {
        if (r == null)
            return this
        if (type != r.type)
            return this
        if (_map != null) {
            r._map!!.forEach {
                _map!![it.key] = it.value
            }
        } else if (_arr != null) {
            r._arr!!.forEach {
                _arr!!.add(it)
            }
        }
        return this
    }

    fun asInteger(def: JsonInteger = 0L): JsonInteger {
        if (_pod == null)
            return def
        if (_pod is Number)
            return (_pod as Number).toLong()
        if (_pod is String)
            return (_pod as String).toLongOrNull() ?: def
        if (_pod is Boolean)
            return if (_pod as Boolean) 1 else 0
        return def
    }

    fun asReal(def: JsonReal = 0.0): JsonReal {
        if (_pod == null)
            return def
        if (_pod is Number)
            return (_pod as Number).toDouble()
        if (_pod is String)
            return (_pod as String).toDoubleOrNull() ?: def
        if (_pod is Boolean)
            return if (_pod as Boolean) 1.0 else 0.0
        return def
    }

    fun asString(def: String = ""): String {
        if (_pod == null)
            return def
        if (_pod is Number)
            return (_pod as Number).toString()
        if (_pod is String)
            return _pod as String
        if (_pod is Boolean)
            return if (_pod as Boolean) "true" else "false"
        return def
    }

    fun asBoolean(def: JsonBoolean = false): JsonBoolean {
        if (_pod == null)
            return def
        if (_pod is Number)
            return (_pod as Number).toFloat() > 0
        if (_pod is String)
            return ((_pod as String).toFloatOrNull() ?: 0f) > 0
        if (_pod is Boolean)
            return _pod as Boolean
        return def
    }

    fun isInteger(): Boolean {
        return _pod is Long
    }

    fun isString(): Boolean {
        return _pod is String
    }

    fun isReal(): Boolean {
        return _pod is Double
    }

    fun isBoolean(): Boolean {
        return _pod is Boolean
    }

    fun forEach(lst: (value: JsonObject) -> Unit) {
        if (_arr != null)
            _arr!!.forEach {
                lst(it)
            }
    }

    fun forEach(map: (value: JsonObject, key: Any) -> Unit) {
        if (_map != null)
            _map!!.forEach {
                map(it.value, it.key)
            }
    }

    fun <T> map(lst: (value: JsonObject) -> T): List<T> {
        if (_arr != null)
            return _arr!!.map {
                lst(it)
            }
        return listOf()
    }

    companion object {

        fun <T> UnMap(pojo: T): JsonObject {
            val r = JsonObject()
            r._map = mutableMapOf()

            val mp = ObjectMapper().convertValue(pojo, JsonMapType())
            mp.forEach {
                r._map!![it.key] = JsonObject().from_any(it.value)
            }

            return r
        }

        inline fun <reified T> Map(jsobj: JsonObject): T {
            return ObjectMapper().convertValue(jsobj.flat(), T::class.java)
        }

        fun FromFlat(map: Map<*, *>?): JsonObject {
            val r = JsonObject()
            if (map != null) {
                r.from_map(map)
            }
            return r
        }

        fun FromFlat(lst: List<*>?): JsonObject {
            val r = JsonObject()
            if (lst != null) {
                r.from_list(lst)
            }
            return r
        }

        fun FromFlat(lst: Array<*>?): JsonObject {
            val r = JsonObject()
            if (lst != null) {
                r.from_list(lst)
            }
            return r
        }

        fun FromFlatAny(v: Any?): JsonObject {
            val r = JsonObject()
            if (v != null) {
                r.from_any(v)
            }
            return r
        }
    }
}

fun toJsonObject(v: Any?): JsonObject? {
    if (v == null)
        return null
    if (v is JsonObject)
        return v
    if (v is String)
        return JsonObject().from(v)
    if (v is List<*>)
        return JsonObject.FromFlat(v)
    if (v is Array<*>)
        return JsonObject.FromFlat(v)
    if (v is Map<*, *>)
        return JsonObject.FromFlat(v)
    return JsonObject.FromFlatAny(v)
}

fun toJson(v: Any?): String {
    if (v == null)
        return ""
    if (v is String)
        return v
    if (v is JsonObject)
        return v.toString()
    return ObjectMapper().writeValueAsString(v)
}
