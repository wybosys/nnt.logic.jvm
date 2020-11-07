package com.nnt.core

class Argument {

    companion object {

        fun Flat(vararg v: Any?): Array<Any> {
            val r = mutableListOf<Any>()
            v.forEach {
                if (it is Array<*>) {
                    it.forEach { e ->
                        if (e != null)
                            r.add(e)
                    }
                } else if (it is List<*>) {
                    it.forEach { e ->
                        if (e != null)
                            r.add(e)
                    }
                } else if (it is Set<*>) {
                    it.forEach { e ->
                        if (e != null)
                            r.add(e)
                    }
                } else if (it is Map<*, *>) {
                    it.forEach { _, v ->
                        if (v != null)
                            r.add(v)
                    }
                } else {
                    if (it != null)
                        r.add(it)
                }
            }
            return r.toTypedArray()
        }

    }
}
