package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.flow

class FirebaseAuthRepository(
    ktorClientFactory: KtorClientFactory,
    override val sharedPreference: KmmPreference,
    baseUrl: String,
    override val firebaseAuthenticator: FirebaseAuthenticator,
) : FirebaseAuthRepositoryInterface {
    override val strapiService = StrapiService(ktorClientFactory.build(), baseUrl)

    fun <T> signIn(authCredential: com.google.firebase.auth.AuthCredential): CommonFlow<DataState<T>> =
        flow {
            executeCatching<T>({
                emit(DataState.loading())
                val firebaseToken = firebaseAuthenticator.signIn(AuthCredential(authCredential))
                val response = getUserInformation<T>(firebaseToken)
                sharedPreference.putString(
                    SharedConstants.ACCESS_TOKEN,
                    response.jwt.orEmpty()
                )
                emit(DataState.data(data = response.user))
            }, this)
        }.asCommonFlow()
}