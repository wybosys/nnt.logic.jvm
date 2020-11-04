package com.test.app

import com.nnt.server.Rest
import com.nnt.server.Transaction
import com.test.app.model.Trans
import com.test.app.router.RSample

class Test : Rest() {

    init {
        routers.register(RSample())
    }

    override fun instanceTransaction(): Transaction {
        return Trans()
    }
}
