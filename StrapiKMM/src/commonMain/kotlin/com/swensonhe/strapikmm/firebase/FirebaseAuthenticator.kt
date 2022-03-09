package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.util.CommonFlow
import com.swensonhe.strapikmm.util.DataState
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.AuthCredential

/**
 * Return the firebase token based on the selected method
 */

expect class FirebaseAuthenticator(
    context: Any?,
    firebaseOptions: FirebaseOptions
) {
    fun initialize(options: FirebaseOptions): FirebaseApp

    fun signIn(email: String, password: String): CommonFlow<DataState<String>>

    fun signIn(authCredential: AuthCredential): CommonFlow<DataState<String>>

    fun signIn(token: String): CommonFlow<DataState<String>>

    fun signUp(email: String, password: String): CommonFlow<DataState<String>>

    fun signOut(): CommonFlow<DataState<Unit>>
}