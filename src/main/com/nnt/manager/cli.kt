package com.nnt.manager

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Cli {

    companion object {

        private var _running = false

        fun Run() = GlobalScope.launch {
            async {
                _running = true
                while (_running) {
                    delay(1000)
                }
            }.await()
        }
    }
}