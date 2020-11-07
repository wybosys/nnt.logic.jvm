package com.nnt.store

interface ISession {

    // 关闭
    fun close()

    // 提交
    fun commit()

    // 回退
    fun rollback()
}