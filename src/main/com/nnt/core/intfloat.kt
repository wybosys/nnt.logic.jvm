package com.nnt.core

// 用int来表示float
class IntFloat(ori: Float = 0f, s: Float = 1f) {

    // 最终表示的float
    private var _value: Float = ori / s.toFloat()

    // 当前真实值
    private var _ori: Float = ori

    // 缩放数值
    private var _s: Float = s

    fun toFloat(): Float {
        return _value
    }

    fun toDouble(): Double {
        return _value.toDouble()
    }

    fun toInt(): Int {
        return _value.toInt()
    }

    fun toLong(): Long {
        return _value.toLong()
    }

    fun toBoolean(): Boolean {
        return _value != 0f
    }

    override fun toString(): String {
        return _value.toString()
    }

    // 缩放后的数据，代表真实值
    var value: Float
        get() {
            return this._value
        }
        set(v) {
            this._value = v
            this._ori = v * this._s
        }

    fun setValue(v: Float): IntFloat {
        this.value = v
        return this
    }

    // 缩放前的数据
    var origin: Float
        get() {
            return this._ori
        }
        set(ori) {
            this._ori = ori
            this._value = ori / this._s
        }

    val scale: Float
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

    fun clone(): IntFloat {
        return IntFloat(this._ori, this._s)
    }

    companion object {

        val SCALE_MONEY = 100f
        val SCALE_PERCENTAGE = 10000f

        fun Money(ori: Float = 0f): IntFloat {
            return IntFloat(ori, SCALE_MONEY)
        }

        fun Percentage(ori: Float = 0f): IntFloat {
            return IntFloat(ori, SCALE_PERCENTAGE)
        }

        fun Origin(ori: Any): Float {
            if (ori is IntFloat)
                return ori.origin
            throw Error("对一个不是IntFloat的数据请求Origin")
        }

        fun From(ori: Any, scale: Float): IntFloat {
            if (ori is IntFloat) {
                return IntFloat(ori.origin, scale)
            }
            return IntFloat((ori as Number).toFloat(), scale)
        }

        fun FromValue(
            v: Any, scale: Float
        ): IntFloat {
            if (v is IntFloat) {
                return IntFloat(v.origin, scale)
            }
            return IntFloat(0f, scale).setValue((v as Number).toFloat())
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
}