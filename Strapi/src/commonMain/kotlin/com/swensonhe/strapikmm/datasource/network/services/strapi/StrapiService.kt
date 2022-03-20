package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.StrapiRequestBuilder
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class StrapiService(
    val httpClient: HttpClient,
    baseUrl: String,
) : KmmBaseService(baseUrl) {

    inline fun <reified T> get(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.get<JsonElement>(buildRequest(builder))
            val data = JsonFlatter.flat<T>(json).convert<T>()
            emit(DataState.data(data = data))
        }, this)
    }.asCommonFlow()

    inline fun <reified T> post(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.post<JsonElement>(buildRequest(builder))
            val data = JsonFlatter.flat<T>(json).convert<T>()
            emit(DataState.data(data = data))
        }, this)
    }.asCommonFlow()


    inline fun <reified T> patch(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.patch<JsonElement>(buildRequest(builder))
            val data = JsonFlatter.flat<T>(json).convert<T>()
            emit(DataState.data(data = data))
        }, this)
    }.asCommonFlow()


    inline fun <reified T> put(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.put<JsonElement>(buildRequest(builder))
            val data = JsonFlatter.flat<T>(json).convert<T>()
            emit(DataState.data(data = data))
        }, this)
    }.asCommonFlow()


    inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.delete<JsonElement>(buildRequest(builder))
            val data = JsonFlatter.flat<T>(json).convert<T>()
            emit(DataState.data(data = data))
        }, this)
    }.asCommonFlow()
}

val jsonWithIgnoredUnknownKeys = Json {
    ignoreUnknownKeys = true
}

inline fun <reified T> JsonElement.convert(): T {
    return jsonWithIgnoredUnknownKeys.decodeFromString(this.toString())
}
