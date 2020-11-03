package com.test.app.model

import com.nnt.server.Transaction

class Trans : Transaction() {

    var uid: String? = null
    var sid: String? = null

    override fun sessionId(): String? {
        return sid
    }

    override fun auth(): Boolean {
        return uid != null && sid != null
    }

}