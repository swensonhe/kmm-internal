package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.AuthResponse
import com.swensonhe.strapikmm.model.FirebaseAuthRequest
import com.swensonhe.strapikmm.sharedpreference.KmmPreference

class FirebaseAuthRepository(
    ktorClientFactory: KtorClientFactory,
    baseUrl: String,
    kmmPreference: KmmPreference
) {
    val strapiService = StrapiService(ktorClientFactory.build(), baseUrl, kmmPreference)

    suspend inline fun <reified T> exchangeFirebaseToken(token: String): AuthResponse<T> =
        strapiService.post<AuthResponse<T>> {
            endpoint("/firebase-auth")
            body(FirebaseAuthRequest(token))
        }

    suspend inline fun <reified T> getUser(): T = strapiService.get {
        endpoint("/users/me")
    }
}