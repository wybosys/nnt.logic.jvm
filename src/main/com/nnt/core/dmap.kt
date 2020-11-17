package com.nnt.core

// 双向map，K/V 必须为唯一
class DMap<K, V> : MutableMap<K, V> {

    private val _keys = mutableMapOf<K, V>()
    private val _values = mutableMapOf<V, K>()

    override fun put(key: K, value: V): V? {
        remove(key)

        _keys[key] = value
        _values[value] = key
        return value
    }

    override fun remove(key: K): V? {
        if (!_keys.containsKey(key))
            return null

        val v = _keys[key]
        _keys.remove(key)
        _values.remove(v)
        return v
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, v) ->
            put(k, v)
        }
    }

    override fun clear() {
        _keys.clear()
        _values.clear()
    }

    override val size: Int
        get() {
            return _keys.size
        }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            return _keys.entries
        }

    override val keys: MutableSet<K>
        get() {
            return _keys.keys
        }

    override val values: MutableCollection<V>
        get() {
            return _keys.values
        }

    override fun containsKey(key: K): Boolean {
        return _keys.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return _values.containsKey(value)
    }

    override fun get(key: K): V? {
        return _keys.get(key)
    }

    override fun isEmpty(): Boolean {
        return _keys.isEmpty()
    }

}
