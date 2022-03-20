package com.swensonhe.strapikmm.datasource.network

import io.ktor.client.*

expect class KtorClientFactory(context: Any){

    fun build(): HttpClient
}
