package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel

var strapiNetworkLogLevel =  NetworkLogLevel.NONE

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

