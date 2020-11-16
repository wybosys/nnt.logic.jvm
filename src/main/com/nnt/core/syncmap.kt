package com.nnt.core

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

// 类似于go的SyncMap，提供可以异步使用的读写map
class SyncMap<K, V> : MutableMap<K, V> {

    private val _map = mutableMapOf<K, V>()
    private val _mtx = ReentrantReadWriteLock()

    val lock: ReentrantReadWriteLock get() = _mtx

    inline fun <T> read(statement: () -> T): T {
        lock.read {
            return statement()
        }
    }

    inline fun <T> write(statement: () -> T): T {
        lock.write {
            return statement()
        }
    }

    override fun put(key: K, value: V): V? {
        _mtx.write {
            return _map.put(key, value)
        }
    }

    override fun remove(key: K): V? {
        _mtx.write {
            return _map.remove(key)
        }
    }

    override fun putAll(from: Map<out K, V>) {
        _mtx.write {
            _map.putAll(from)
        }
    }

    override fun clear() {
        _mtx.write {
            _map.clear()
        }
    }

    override val size: Int
        get() {
            _mtx.read {
                return _map.size
            }
        }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            _mtx.read {
                return _map.entries
            }
        }

    override val keys: MutableSet<K>
        get() {
            _mtx.read {
                return _map.keys
            }
        }

    override val values: MutableCollection<V>
        get() {
            _mtx.read {
                return _map.values
            }
        }

    override fun containsKey(key: K): Boolean {
        _mtx.read {
            return _map.containsKey(key)
        }
    }

    override fun containsValue(value: V): Boolean {
        _mtx.read {
            return _map.containsValue(value)
        }
    }

    override fun get(key: K): V? {
        _mtx.read {
            return _map.get(key)
        }
    }

    override fun isEmpty(): Boolean {
        _mtx.read {
            return _map.isEmpty()
        }
    }
}
