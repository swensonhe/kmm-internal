package com.swensonhe.strapikmm.datasource.network

import android.content.Context
import com.liftric.kvault.KVault
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

actual class KtorClientFactory actual constructor(context: Any) {
    private val preference = KmmPreference(KVault(context as Context))

    actual fun build(): HttpClient {
        val jsonSerializer = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
            useAlternativeNames = false
        }

        return HttpClient(Android) {

            install(ContentNegotiation) {
                json()
            }

            install(DefaultRequest) {
                val token = preference.getString(SharedConstants.ACCESS_TOKEN)
                if (token.isNullOrEmpty().not()) {
                    headers.append(
                        SharedConstants.AUTHORIZATION_HEADER,
                        "${SharedConstants.BEARER} $token"
                    )
                }
            }

            HttpResponseValidator {
                handleResponseException { cause ->
                    val responseException =
                        cause as? ResponseException ?: return@handleResponseException
                    val response = responseException.response
                    val bytes = response.body<JsonElement>()
                    val errorData =
                        JsonFlatter.flat<NetworkError>(jsonSerializer.decodeFromJsonElement(bytes))
                    val errorResponse =
                        jsonSerializer.decodeFromJsonElement<NetworkError>(errorData)
                    val error = NetworkErrorMapper().mapServerError(
                        errorCode = errorResponse.code,
                        errorMessage = errorResponse.message,
                        throwable = responseException
                    )
                    throw error
                }
            }
        }
    }
}