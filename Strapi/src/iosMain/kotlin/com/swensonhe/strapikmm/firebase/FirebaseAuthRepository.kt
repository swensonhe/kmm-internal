package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.model.OAuthProviderCredential
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.OAuthProvider
import kotlinx.coroutines.flow.flow

actual class FirebaseAuthRepository actual constructor(
    ktorClientFactory: KtorClientFactory,
    override val sharedPreference: KmmPreference,
    baseUrl: String,
    override val firebaseAuthenticator: FirebaseAuthenticator,
) : FirebaseAuthRepositoryInterface() {
    override val strapiService = StrapiService(ktorClientFactory.build(), baseUrl)

    actual inline fun <reified T> signIn(authCredential: Any): CommonFlow<DataState<T>> =
        flow {
            executeCatching<T>({
                val oAuthProviderCredential = authCredential as OAuthProviderCredential
                val credential = OAuthProvider.credential(
                    providerId = oAuthProviderCredential.providerId,
                    idToken = oAuthProviderCredential.idToken,
                    rawNonce = oAuthProviderCredential.rawNonce,
                    accessToken = oAuthProviderCredential.accessToken
                )
                emit(DataState.loading())
                val firebaseToken = firebaseAuthenticator.signIn(credential)
                val response = exchangeFirebaseToken<T>(firebaseToken)
                sharedPreference.putString(
                    SharedConstants.ACCESS_TOKEN,
                    response.jwt.orEmpty()
                )
                emit(DataState.data<T>(data = response.user))
            }, this)
        }.asCommonFlow()
}