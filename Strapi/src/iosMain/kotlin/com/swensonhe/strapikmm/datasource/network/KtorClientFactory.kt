package com.swensonhe.strapikmm.datasource.network

import com.liftric.kvault.KVault
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.LoggerConfiguration
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.ios.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

actual class KtorClientFactory actual constructor(context: Any, val networkLogLevel: NetworkLogLevel) {
    private val preference = KmmPreference(KVault())

    init {
        LoggerConfiguration.networkLogLevel = networkLogLevel
    }

    actual fun build(): HttpClient {
        val jsonSerializer = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
            explicitNulls = false
            useAlternativeNames = false
        }

        return HttpClient(Ios) {
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