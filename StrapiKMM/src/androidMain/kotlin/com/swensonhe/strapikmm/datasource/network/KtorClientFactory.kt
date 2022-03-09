package com.swensonhe.strapikmm.datasource.network

import com.liftric.kvault.KVault
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.datasource.network.services.strapi.convert
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.util.KmmContext
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.request.*

actual class KtorClientFactory(context: KmmContext) {
    private val preference = KmmPreference(KVault(context))

    actual fun build(): HttpClient {
        val jsonSerializer = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }
        val kotlinSerializer = KotlinxSerializer(jsonSerializer)

        return HttpClient(Android){
            install(Logging){
                level = LogLevel.ALL
            }

            val token = preference.getString(SharedConstants.ACCESS_TOKEN)
            install(DefaultRequest) {
                if (token.isNullOrEmpty().not()) {
                    headers.append(SharedConstants.AUTHORIZATION_HEADER, "${SharedConstants.BEARER} $token")
                }
            }

            install(JsonFeature){
                serializer = kotlinSerializer
            }

            HttpResponseValidator {
                handleResponseException { cause ->
                    val responseException = cause as? ResponseException ?: return@handleResponseException
                    val response = responseException.response
                    val bytes = response.receive<ByteArray>()
                    val errorResponse = JsonFlatter.flat<NetworkError>(jsonSerializer.parseToJsonElement(bytes.decodeToString())).convert<NetworkError>()
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