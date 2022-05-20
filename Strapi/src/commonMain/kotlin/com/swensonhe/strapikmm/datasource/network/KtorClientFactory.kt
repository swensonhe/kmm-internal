package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.Logger
import io.ktor.client.*
import io.ktor.client.request.*

enum class NetworkLogLevel {
    NONE,
    REQUEST,
    ALL
}

expect class KtorClientFactory(context: Any, networkLogLevel: NetworkLogLevel) {

    fun build(): HttpClient
}

fun HttpRequestBuilder.printCURLDescription(
    bodyString: String? = null,
    method: String,
    kmmPreference: KmmPreference
) {
    val url = url
    val urlBuilder = url.buildString()
    Logger("").log("================================================")

    val components = mutableListOf<String>()
    components.add("$ curl -v")
    components.add("-X $method")

    val headers = headers.entries()

    headers.forEach { entry ->
        entry.value.forEach { value ->
            components.add("-H \"${entry.key}: ${value.replace("\"", "\\\"")}\"")
        }
    }

    val token = kmmPreference.getString(SharedConstants.ACCESS_TOKEN)
    if (token.isNullOrEmpty().not()) {
        components.add("-H \"Authorization: Bearer ${token!!.replace("\"", "\\\"")}\"")
    }

    if (bodyString != null) {
        components.add("-d \"${bodyString.replace("\\\"", "\\\\\"").replace("\"", "\\\"")}\"")
    }
    components.add("\"$urlBuilder\"")

    val message = components.joinToString(" \\\n\t")
    Logger("").log(message)
    Logger("").log("================================================")
}