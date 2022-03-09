package com.swensonhe.strapikmm.datasource.network

import io.ktor.client.*

expect class KtorClientFactory {

    fun build(): HttpClient
}
