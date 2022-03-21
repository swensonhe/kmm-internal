package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState

expect class FirebaseAuthRepository(
    ktorClientFactory: KtorClientFactory,
    sharedPreference: KmmPreference,
    baseUrl: String,
    firebaseAuthenticator: FirebaseAuthenticator,
) : FirebaseAuthRepositoryInterface {

    inline fun <reified T> signIn(authCredential: Any): CommonFlow<DataState<T>>
}