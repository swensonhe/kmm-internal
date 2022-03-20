package com.swensonhe.strapikmm.datasource.network

import io.ktor.client.request.*

open class KmmBaseService(private val baseUrl: String) {
    fun buildRequest(
        requestBuilder: StrapiRequestBuilder,
    ): HttpRequestBuilder {
        val builderData = requestBuilder.build()
        val urlSuffix = builderData.first
        val headers = builderData.second.filterIsInstance<RequestContent.Header>()
        val queries = builderData.second.filterIsInstance<RequestContent.Query>()
        val body = builderData.second.filterIsInstance<RequestContent.Body<*>>()

        return HttpRequestBuilder().apply {
            url(baseUrl + urlSuffix)

            queries.forEach { param ->
                parameter(param.key, param.value)
            }

            headers.forEach {
                header(it.key, it.value)
            }

            if (body.isNotEmpty()) {
                this.body = body.first().value!!
            }
        }
    }
}