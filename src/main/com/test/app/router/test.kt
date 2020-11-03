package com.test.app.router

import com.nnt.core.action
import com.test.app.model.Echoo
import com.test.app.model.Trans

class RTest {

    @action(Echoo::class)
    fun echo(trans: Trans) {

    }

}