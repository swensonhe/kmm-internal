package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.util.Logger
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

expect class KtorClientFactory(context: Any) {

    fun build(): HttpClient
}

fun HttpRequestBuilder.printCURLDescription(bodyString: String? = null) {
    val url = url
    val urlBuilder = url.buildString()
    val method = method
    Logger("").log("================================================\n")

    val components = mutableListOf<String>()
    components.add("$ curl -v")
    components.add("-X ${method.value}")

    val headers = headers.entries()

    headers.forEach { entry ->
        entry.value.forEach { value ->
            components.add("-H ${entry.key}: ${value.replace("\"", "\\\"")}")
        }
    }

    if (bodyString != null) {
        components.add("-d ${bodyString.replace("\\\"", "\\\\\"").replace("\"", "\\\"")}")
    }
    components.add("\"$urlBuilder\"")

    val message = components.joinToString(" \\\n\t")
    Logger("").log(message)
    Logger("").log("================================================\n")
}