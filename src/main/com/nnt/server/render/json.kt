package com.nnt.server.render

import com.nnt.core.Output
import com.nnt.core.STATUS
import com.nnt.server.Transaction
import com.nnt.server.TransactionSubmitOption
import org.springframework.util.MimeType

class Json : AbstractRender() {

    init {
        type = MimeType.valueOf("json").toString()
    }

    override fun render(trans: Transaction, opt: TransactionSubmitOption?): ByteArray {
        var r = mutableMapOf<String, Any?>()

        // 填充模型数据
        if (opt != null && opt.model) {
            if (opt.raw) {
                val str = trans.model.toString()
                return str.toByteArray()
            }

            val tr = Output(trans.model)
            if (trans.model != null && tr != null)
                r = tr
        } else {
            r["code"] = trans.status
            if (trans.status != STATUS.OK) {
                r["message"] = trans.message
            } else {
                if (opt != null && opt.raw) {
                    r["data"] = trans.model
                } else {
                    r["data"] = Output(trans.model)
                }
                // 保护data不为null
                if (r["data"] == null && trans.model != null)
                    r["data"] = mapOf<String, Any>()
            }
        }

        // 填充框架通信数据
        val cmid = trans.params["_cmid"]
        if (cmid != null && cmid is Number)
            r["_cmid"] = cmid.toLong()
        val listen = trans.params["_listening"]
        if (listen != null && listen is Number)
            r["_listening"] = listen.toInt()

        // 转换成数据流输出
        //val str = toJson(r)
        //return str.toByteArray()
        return "".toByteArray()
    }

}