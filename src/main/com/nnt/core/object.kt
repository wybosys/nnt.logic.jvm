package com.nnt.core

interface ISerializable {

    // 成功返回array，不成功返回null
    fun serialize(): ByteArray?

    // 成功返回自身，不成功返回null
    fun desserialize(buf: ByteArray): ISerializable?
}

interface IStringable {

    // 区别于toString，tostr可以失败，返回null
    fun tostr(): String?

    // 从string读取，失败返回null
    fun fromstr(str: String): IStringable?
}