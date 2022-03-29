package com.swensonhe.strapikmm.util

actual class Logger actual constructor(
    private val className: String
) {

    actual fun log(msg: String) {
        if (!BuildConfig().isDebug()) {
            // production logging - Crashlytics or something else
        }
        else{
            if (className.isEmpty()) {
                printLogD(msg)
            } else {
                printLogD("$className: $msg")
            }
        }
    }
}






