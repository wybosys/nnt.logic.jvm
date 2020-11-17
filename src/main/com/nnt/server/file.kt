package com.nnt.server

import com.nnt.core.Stats
import io.vertx.core.buffer.Buffer

// 专用来返回的文件对象
class RespFile {

    protected var _file: String? = null
    protected var _buf: Buffer? = null
    var type: String? = null
    protected var _stat: Stats? = null
    protected var _cachable: Boolean = true

    val length: Int
        get() {
            if (_buf != null)
                return _buf!!.length()
            return 0
        }

    protected var _downloadfile: String? = null

    fun asDownload(fn: String): RespFile {
        _downloadfile = fn
        return this
    }

    val cachable: Boolean
        get() {
            return _stat != null
        }

    val stat: Stats?
        get() {
            return _stat
        }

    val download: Boolean
        get() {
            return _downloadfile != null
        }

    val file: String?
        get() {
            if (_file != null)
                return _file
            return null
        }

    companion object {

        // 原始数据流
        fun Buffer(buf: Buffer, typ: String? = null): RespFile {
            val r = RespFile()
            r.type = typ
            r._buf = buf
            return r
        }

        fun Plain(txt: String, typ: String? = null): RespFile {
            val r = RespFile()
            r.type = typ
            r._buf = Buffer.buffer(txt)
            return r
        }

    }
}