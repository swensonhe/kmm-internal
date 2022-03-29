package com.swensonhe.strapikmm.datasource.network

import com.liftric.kvault.KVault
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.ios.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromJsonElement

actual class KtorClientFactory actual constructor(context: Any) {
    private val preference = KmmPreference(KVault())

    actual fun build(): HttpClient {
        val jsonSerializer = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
            useAlternativeNames = false
        }

        return HttpClient(Ios) {

            install(JsonFeature) {
                serializer = KotlinxSerializer()
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
                    val bytes = response.receive<ByteArray>()
                    val string = bytes.decodeToString()
                    val errorData =
                        JsonFlatter.flat<NetworkError>(jsonSerializer.decodeFromString(string))
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