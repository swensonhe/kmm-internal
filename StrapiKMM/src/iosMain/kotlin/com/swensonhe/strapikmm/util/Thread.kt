package com.swensonhe.strapikmm.util

import kotlin.native.concurrent.freeze

@ThreadLocal
object Threading {
    fun <T> T.share(): T {
        return this.freeze()
    }
}