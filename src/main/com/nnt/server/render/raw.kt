package com.nnt.server.render

import com.nnt.server.Transaction
import com.nnt.server.TransactionSubmitOption

class Raw : AbstractRender() {

    init {
        type = "text/plain"
    }

    override fun render(trans: Transaction, opt: TransactionSubmitOption?): ByteArray {
        val str = trans.model.toString()
        return str.toByteArray()
    }

}
