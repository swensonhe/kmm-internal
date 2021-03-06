package com.swensonhe.strapikmm.util

actual class Logger actual constructor(
    private val className: String
) {

    actual fun log(msg: String) {
        if(!BuildConfig().isDebug()){
            // Crashlytics or whatever
        }
        else {
            if (className.isEmpty()) {
                println(msg)
            } else {
                println("$className: $msg")
            }
        }
    }
}