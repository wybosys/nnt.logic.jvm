package com.nnt.store

import com.nnt.core.Jsonobj

const val DEFAULT_PORT = 3306

class RMysql : AbstractRdb() {

    override fun config(cfg: Jsonobj): Boolean {
        if (!super.config(cfg))
            return false
        return true
    }

    override suspend fun open() {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        TODO("Not yet implemented")
    }

}
