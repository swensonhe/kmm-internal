package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.model.AuthResponse
import com.swensonhe.strapikmm.model.FirebaseAuthRequest
import com.swensonhe.strapikmm.model.ItemResponse
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

abstract class FirebaseAuthRepositoryInterface {
    abstract val strapiService: StrapiService
    abstract val firebaseAuthenticator: FirebaseAuthenticator
    abstract val sharedPreference: KmmPreference

    inline fun <reified T> signIn(token: String): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val firebaseToken = firebaseAuthenticator.signIn(token)
            val response = exchangeFirebaseToken<T>(firebaseToken)
            sharedPreference.putString(SharedConstants.ACCESS_TOKEN, response.jwt.orEmpty())
            emit(DataState.data<T>(data = response.user))
        }, this)
    }.asCommonFlow()

    inline fun <reified T> signUp(email: String, password: String): CommonFlow<DataState<T>> =
        flow {
            executeCatching<T>({
                emit(DataState.loading())
                val firebaseToken = firebaseAuthenticator.signUp(email, password)
                val response = exchangeFirebaseToken<T>(firebaseToken)
                sharedPreference.putString(SharedConstants.ACCESS_TOKEN, response.jwt.orEmpty())
                emit(DataState.data<T>(data = response.user))
            }, this)
        }.asCommonFlow()

    fun signOut(): CommonFlow<DataState<Unit>> = flow {
        executeCatching<Unit>({
            emit(DataState.loading())
            firebaseAuthenticator.signOut()
            emit(DataState.data(data = Unit))
        }, this)
    }.asCommonFlow()

    inline fun <reified T> signIn(): CommonFlow<DataState<T>> = flow {
        executeCatching<T>({
            emit(DataState.loading())
            val firebaseToken = firebaseAuthenticator.signIn()
            val response = exchangeFirebaseToken<T>(firebaseToken)
            sharedPreference.putString(SharedConstants.ACCESS_TOKEN, response.jwt.orEmpty())
            emit(DataState.data<T>(data = response.user))
        }, this)
    }.asCommonFlow()

    fun isTokenExist(): Boolean = firebaseAuthenticator.isTokenExist()

    suspend inline fun <reified T> exchangeFirebaseToken(token: String): AuthResponse<T> {
        sharedPreference.clearValue(SharedConstants.ACCESS_TOKEN)
        return suspendCancellableCoroutine { continuation ->
            strapiService.post<AuthResponse<T>> {
                endpoint("/firebase-auth")
                body(FirebaseAuthRequest(token))
            }.collectCommon {
                it.data?.let {
                    continuation.resumeWith(
                        Result.success(it)
                    )
                }
                it.error?.let {
                    continuation.resumeWith(
                        Result.failure(it)
                    )
                }
            }
        }
    }

    inline fun <reified T> signIn(email: String, password: String): CommonFlow<DataState<T>> =
        flow {
            executeCatching<T>({
                emit(DataState.loading())
                val firebaseToken = firebaseAuthenticator.signIn(email, password)
                val response = exchangeFirebaseToken<T>(firebaseToken)
                sharedPreference.putString(SharedConstants.ACCESS_TOKEN, response.jwt.orEmpty())
                emit(DataState.data<T>(data = response.user))
            }, this)
        }.asCommonFlow()

    inline fun <reified T> getUser(): CommonFlow<DataState<T>> = strapiService.get {
        endpoint("/users/me")
    }
}