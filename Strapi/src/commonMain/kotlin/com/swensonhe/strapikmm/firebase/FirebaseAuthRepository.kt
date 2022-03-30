package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.AuthResponse
import com.swensonhe.strapikmm.model.FirebaseAuthRequest
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState

class FirebaseAuthRepository(
    ktorClientFactory: KtorClientFactory,
    baseUrl: String,
) {
    val strapiService = StrapiService(ktorClientFactory.build(), baseUrl)

    inline fun <reified T> exchangeFirebaseToken(token: String): CommonFlow<DataState<AuthResponse<T>>> =
        strapiService.post<AuthResponse<T>> {
            endpoint("/firebase-auth")
            body(FirebaseAuthRequest(token))
        }

    inline fun <reified T> getUser(): CommonFlow<DataState<T>> = strapiService.get {
        endpoint("/users/me")
    }
}