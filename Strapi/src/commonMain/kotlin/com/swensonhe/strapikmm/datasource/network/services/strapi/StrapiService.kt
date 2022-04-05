package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.StrapiRequestBuilder
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class StrapiService(
    val httpClient: HttpClient,
    baseUrl: String,
    kmmPreference: KmmPreference
) : KmmBaseService(baseUrl, kmmPreference) {

    inline fun <reified T> get(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.get<JsonElement>(buildRequest(builder, HttpMethod.Get.value))
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
            val json = httpClient.post<JsonElement>(buildRequest(builder, HttpMethod.Post.value))
            if (T::class.simpleName == Unit::class.simpleName) {
                emit(DataState.data(data = Unit as T))
            } else {
                val data = JsonFlatter.flat<T>(json).convert<T>()
                emit(DataState.data(data = data))
            }
        }, this)
    }.asCommonFlow()


    inline fun <reified T> patch(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.patch<JsonElement>(buildRequest(builder, HttpMethod.Patch.value))
            if (T::class.simpleName == Unit::class.simpleName) {
                emit(DataState.data(data = Unit as T))
            } else {
                val data = JsonFlatter.flat<T>(json).convert<T>()
                emit(DataState.data(data = data))
            }
        }, this)
    }.asCommonFlow()

    inline fun <reified T> put(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.put<JsonElement>(buildRequest(builder, HttpMethod.Put.value))
            if (T::class.simpleName == Unit::class.simpleName) {
                emit(DataState.data(data = Unit as T))
            } else {
                val data = JsonFlatter.flat<T>(json).convert<T>()
                emit(DataState.data(data = data))
            }
        }, this)
    }.asCommonFlow()


    inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()
            val json = httpClient.delete<JsonElement>(buildRequest(builder, HttpMethod.Delete.value))
            if (T::class.simpleName == Unit::class.simpleName) {
                emit(DataState.data(data = Unit as T))
            } else {
                val data = JsonFlatter.flat<T>(json).convert<T>()
                emit(DataState.data(data = data))
            }
        }, this)
    }.asCommonFlow()
}

val jsonWithIgnoredUnknownKeys = Json {
    ignoreUnknownKeys = true
    useAlternativeNames = false
    encodeDefaults = false
}

inline fun <reified T> JsonElement.convert(): T {
    return jsonWithIgnoredUnknownKeys.decodeFromString(this.toString())
}
