package com.swensonhe.strapikmm.firebase

import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AuthCredential

/**
 * Return the firebase token based on the selected method
 */

expect class FirebaseAuthenticator(
    context: Any?,
    firebaseOptions: FirebaseOptions
) {
    fun initialize(options: FirebaseOptions): FirebaseApp

    fun isTokenExist(): Boolean

    suspend fun signIn(email: String, password: String): String

    suspend fun sendSignInLinkToEmail(email: String, settings: ActionCodeSettings)

    suspend fun signInWithEmailLink(email: String, link: String): String

    suspend fun signIn(authCredential: AuthCredential): String

    suspend fun signIn(token: String): String

    suspend fun signIn(): String

    suspend fun signUp(email: String, password: String): String

    suspend fun signOut()
}