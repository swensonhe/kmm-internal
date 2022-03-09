package com.swensonhe.strapikmm.firebase

import com.liftric.kvault.KVault
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import com.swensonhe.strapikmm.util.asCommonFlow
import com.swensonhe.strapikmm.errorhandling.AppException
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.errorhandling.executeCatching
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

actual class FirebaseAuthenticator actual constructor(
    private val context: Any?,
    firebaseOptions: FirebaseOptions
) {
    private val firebaseAuth: FirebaseAuth

    init {
        val firebaseApp = initialize(firebaseOptions)
        firebaseAuth = Firebase.auth(firebaseApp)
    }

    actual fun initialize(
        options: FirebaseOptions
    ): FirebaseApp {
        return Firebase.initialize(context, options)
    }

    actual fun signIn(
        email: String,
        password: String
    ): CommonFlow<DataState<String>> = flow {
        executeCatching<String>({
            emit(DataState.loading())
            val user = firebaseAuth.signInWithEmailAndPassword(email, password)
            val token = user.user?.getIdToken(true)
            emit(DataState.data(data = token))
        }, this)
    }.asCommonFlow()

    actual fun signUp(
        email: String,
        password: String
    ): CommonFlow<DataState<String>> = flow {
        executeCatching<String>({
            emit(DataState.loading())
            val user = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val token = user.user?.getIdToken(true)
            emit(DataState.data(data = token))
        }, this)
    }.asCommonFlow()

    actual fun signOut(): CommonFlow<DataState<Unit>> = flow {
        executeCatching<Unit>({
            emit(DataState.loading())
            firebaseAuth.signOut()
            emit(DataState.data<Unit>(data = Unit))
        }, this)
    }.asCommonFlow()

    actual fun signIn(authCredential: AuthCredential) = flow {
        executeCatching<String>({
            emit(DataState.loading())
            val user = firebaseAuth.signInWithCredential(authCredential)
            val token = user.user?.getIdToken(true)
            emit(DataState.data(data = token))
        }, this)
    }.asCommonFlow()

    actual fun signIn(token: String) = flow {
        executeCatching<String>({
            emit(DataState.loading())
            val user = firebaseAuth.signInWithCustomToken(token)
            val firebaseToken = user.user?.getIdToken(true)
            emit(DataState.data(data = firebaseToken))
        }, this)
    }.asCommonFlow()
}