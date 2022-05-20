package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object LoggerConfiguration {
    var networkLogLevel =  NetworkLogLevel.ALL
}

expect class Logger(
    className: String,
) {
    fun log(msg: String)
}

fun printLogD(className: String?, message: String ) {
    println("$className: $message")
}

fun printLogD(message: String ) {
    println(message)
}

