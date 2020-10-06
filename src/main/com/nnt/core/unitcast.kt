package com.nnt.core

// 单位转换

// 字节单位转换
class ByteUnit : IStringable, Comparable<ByteUnit> {

    private var _K: Int = 0
    private var _dirty = false

    // 单位
    var u: Int
        get() {
            return _K
        }
        set(value) {
            if (_K == value)
                return
            var old: Long = 0
            if (_K > 0) {
                old = bytes
            }
            _K = value
            if (old > 0) {
                bytes = old
            }
        }

    init {
        u = 1024
    }

    private var _p: Int = 0
    private var _t: Int = 0
    private var _g: Int = 0
    private var _m: Int = 0
    private var _k: Int = 0
    private var _b: Int = 0

    var p: Int
        get() {
            return _p
        }
        set(value) {
            if (_p != value) {
                _p = value
                _dirty = true
            }
        }

    var t: Int
        get() {
            return _t
        }
        set(value) {
            if (value > _K) {
                _t = value % _K
                p += value / _K
                _dirty = true
            } else if (_t != value) {
                _t = value
                _dirty = true
            }
        }

    var g: Int
        get() {
            return _g
        }
        set(value) {
            if (value > _K) {
                _g = value % _K
                t += value / _K
                _dirty = true
            } else if (_g != value) {
                _g = value
                _dirty = true
            }
            _dirty = true
        }

    var m: Int
        get() {
            return _m
        }
        set(value) {
            if (value > _K) {
                _m = value % _K
                g += value / _K
                _dirty = true
            } else if (_m != value) {
                _m = value
                _dirty = true
            }
        }

    var k: Int
        get() {
            return _k
        }
        set(value) {
            if (value > _K) {
                _k = value % _K
                m += value / _K
                _dirty = true
            } else if (_k != value) {
                _k = value
                _dirty = true
            }
        }

    var b: Int
        get() {
            return _b
        }
        set(value) {
            if (value > _K) {
                _b = value % _K
                k += value / _K
                _dirty = true
            } else if (_b != value) {
                _b = value
                _dirty = true
            }
        }

    override fun toString(): String {
        return tostr()!!
    }

    override fun tostr(): String? {
        val a = mutableListOf<String>()
        if (p > 0)
            a.add("${p}P")
        if (t > 0)
            a.add("${t}T")
        if (g > 0)
            a.add("${g}G")
        if (m > 0)
            a.add("${m}M")
        if (k > 0)
            a.add("${k}K")
        if (b > 0)
            a.add("${b}")
        return a.joinToString("")
    }

    override fun fromstr(str: String): IStringable? {
        var p = 0
        var t = 0
        var g = 0
        var m = 0
        var k = 0
        var b = 0

        var cache = ""
        var hasdot = false
        for (e in str) {
            when (e.toUpperCase()) {
                'P' -> {
                    if (cache.isEmpty()) {
                        hasdot = false
                        continue
                    }
                    if (hasdot) {
                        var f = cache.toDouble()
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            p = x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            t = x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            g = x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            m = x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            k = x
                        } else {
                            f *= _K
                        }
                        if (f >= 0) {
                            b = f.toInt()
                        }
                    } else {
                        p = cache.toInt()
                    }
                    cache = ""
                    hasdot = false
                }
                'T' -> {
                    if (cache.isEmpty()) {
                        hasdot = false
                        continue
                    }
                    if (hasdot) {
                        var f = cache.toDouble()
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            t += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            g += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            m += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            k += x
                        } else {
                            f *= _K
                        }
                        if (f >= 0) {
                            b += f.toInt()
                        }
                    } else {
                        t += cache.toInt()
                    }
                    cache = ""
                    hasdot = false
                }
                'G' -> {
                    if (cache.isEmpty()) {
                        hasdot = false
                        continue
                    }
                    if (hasdot) {
                        var f = cache.toDouble()
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            g += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            m += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            k += x
                        } else {
                            f *= _K
                        }
                        if (f >= 0) {
                            b += f.toInt()
                        }
                    } else {
                        g += cache.toInt()
                    }
                    cache = ""
                    hasdot = false
                }
                'M' -> {
                    if (cache.isEmpty()) {
                        hasdot = false
                        continue
                    }
                    if (hasdot) {
                        var f = cache.toDouble()
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            m += x
                        } else {
                            f *= _K
                        }
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            k += x
                        } else {
                            f *= _K
                        }
                        if (f >= 0) {
                            b += f.toInt()
                        }
                    } else {
                        m += cache.toInt()
                    }
                    cache = ""
                    hasdot = false
                }
                'K' -> {
                    if (cache.isEmpty()) {
                        hasdot = false
                        continue
                    }
                    if (hasdot) {
                        var f = cache.toDouble()
                        if (f >= 1) {
                            val x = f.toInt()
                            f = (f - x) * _K
                            k += x
                        } else {
                            f *= _K
                        }
                        if (f >= 0) {
                            b += f.toInt()
                        }
                    } else {
                        k += cache.toInt()
                    }
                    cache = ""
                    hasdot = false
                }
                'B' -> {
                    continue
                }
                else -> {
                    if (e == '.') {
                        if (hasdot)
                            return null
                        hasdot = true
                    } else if (!e.isDigit()) {
                        return null
                    }
                    cache += e
                }
            }
        }

        if (!cache.isEmpty()) {
            b += cache.toInt()
        }

        this.p = p
        this.t = t
        this.g = g
        this.m = m
        this.k = k
        this.b = b

        return this
    }

    private var _bytes: Long = 0

    // 转换为字节数
    var bytes: Long
        get() {
            if (_dirty) {
                _bytes = 0
                var s: Long = _K.toLong()
                if (_b > 0)
                    _bytes += _b
                if (_k > 0)
                    _bytes += _k * s
                s *= _K
                if (_m > 0)
                    _bytes += _m * s
                s *= _K
                if (_g > 0)
                    _bytes += _g * s
                s *= _K
                if (_t > 0)
                    _bytes += _t * s
                s *= _K
                if (_p > 0)
                    _bytes += _p * s
                _dirty = false
            }
            return _bytes
        }
        set(value) {
            var v = value
            _b = (v % _K).toInt()
            v /= _K
            _k = (v % _K).toInt()
            v /= _K
            _m = (v % _K).toInt()
            v /= _K
            _g = (v % _K).toInt()
            v /= _K
            _t = (v % _K).toInt()
            v /= _K
            _p = (v % _K).toInt()
            _dirty = false
        }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ByteUnit) {
            return p == other.p && t == other.t && g == other.g && m == other.m && k == other.k && b == other.b
        }
        if (other is Number) {
            return bytes == other
        }
        return false
    }

    override fun compareTo(other: ByteUnit): Int {
        return bytes.compareTo(other.bytes)
    }

}
