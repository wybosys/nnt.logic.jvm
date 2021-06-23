package com.nnt.core

import java.net.ServerSocket

open class Socket {

    companion object {

        fun RandomPort(): Int {
            val sck = ServerSocket(0)
            val port = sck.localPort
            sck.close()
            return port
        }

    }

}