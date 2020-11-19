package com.nnt.core

/** 用int来表示float
 * @param ori 缩放后的数值
 * @param s 缩放倍数
 */
class IntFloat(ori: Int = 0, s: Int = 1) : Number(), Comparable<Float>, Cloneable {

    // 最终表示的float
    private var _value: Float = ori / s.toFloat()

    // 缩放后数值
    private var _ori: Int = ori

    // 缩放数值
    private var _s: Int = s

    override fun toFloat(): Float {
        return _value
    }

    override fun toByte(): Byte {
        return _value.toByte()
    }

    override fun toChar(): Char {
        return _value.toChar()
    }

    override fun toDouble(): Double {
        return _value.toDouble()
    }

    override fun toInt(): Int {
        return _value.toInt()
    }

    override fun toLong(): Long {
        return _value.toLong()
    }

    override fun toShort(): Short {
        return _value.toShort()
    }

    fun toBoolean(): Boolean {
        return _value != 0f
    }

    override fun toString(): String {
        return _value.toString()
    }

    // 真实值
    var value: Float
        get() {
            return this._value
        }
        set(v) {
            this._value = v
            this._ori = (v * this._s).toInt()
        }

    fun setValue(v: Float): IntFloat {
        this.value = v
        return this
    }

    // 缩放后的数据
    var origin: Int
        get() {
            return this._ori
        }
        set(ori) {
            this._ori = ori
            this._value = ori / this._s.toFloat()
        }

    val scale: Int
        get() {
            return this._s
        }

    fun add(r: Float): IntFloat {
        this.value += r
        return this
    }

    fun multiply(r: Float): IntFloat {
        this.value *= r
        return this
    }

    override fun clone(): IntFloat {
        return IntFloat(this._ori, this._s)
    }

    companion object {

        val SCALE_MONEY = 100
        val SCALE_PERCENTAGE = 10000

        fun Money(ori: Int = 0): IntFloat {
            return IntFloat(ori, SCALE_MONEY)
        }

        fun Percentage(ori: Int = 0): IntFloat {
            return IntFloat(ori, SCALE_PERCENTAGE)
        }

        fun Origin(ori: Any): Float {
            if (ori is IntFloat)
                return ori.origin.toFloat()
            throw Error("对一个不是IntFloat的数据请求Origin")
        }

        fun From(ori: Any, scale: Int): IntFloat {
            if (ori is IntFloat) {
                return IntFloat(ori.origin, scale)
            }
            return IntFloat((ori as Number).toInt(), scale)
        }

        fun FromValue(
            v: Any, scale: Int,
        ): IntFloat {
            if (v is IntFloat) {
                return IntFloat(v.origin, scale)
            }
            return IntFloat(0, scale).setValue((v as Number).toFloat())
        }

        fun Multiply(l: Any, r: Float): IntFloat {
            if (l is IntFloat) {
                return l.clone().multiply(r)
            }
            throw Error("对一个不是IntFloat的数据进行multiply操作")
        }

        fun Add(l: Any, r: Float): IntFloat {
            if (l is IntFloat) {
                return l.clone().add(r)
            }
            throw Error("对一个不是IntFloat的数据进行multiply操作")
        }
    }

    override fun compareTo(other: Float): Int {
        return ((value - other) * 1000).toInt()
    }

    operator fun plus(v: Number): IntFloat {
        val r = clone()
        r += v
        return r
    }

    operator fun plusAssign(v: Number) {
        value += v.toFloat()
    }

    operator fun minus(v: Number): IntFloat {
        val r = clone()
        r -= v
        return r
    }

    operator fun minusAssign(v: Number) {
        value -= v.toFloat()
    }

    operator fun times(v: Number): IntFloat {
        val r = clone()
        r *= v
        return r
    }

    operator fun timesAssign(v: Number) {
        value *= v.toFloat()
    }

    operator fun div(v: Number): IntFloat {
        val r = clone()
        r /= v
        return r
    }

    operator fun divAssign(v: Number) {
        value /= v.toFloat()
    }
}