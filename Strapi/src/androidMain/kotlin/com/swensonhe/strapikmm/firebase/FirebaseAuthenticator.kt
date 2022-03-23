package com.swensonhe.strapikmm.firebase

import com.swensonhe.strapikmm.errorhandling.executeCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.initialize

actual class FirebaseAuthenticator actual constructor(
    private val context: Any?,
    firebaseOptions: FirebaseOptions
) {
    private val firebaseAuth: FirebaseAuth

    init {
        val firebaseApp = initialize(firebaseOptions)
        firebaseAuth = Firebase.auth(firebaseApp)
    }

    actual fun initialize(options: FirebaseOptions): FirebaseApp {
        return Firebase.initialize(context, options, "android")
    }

    actual suspend fun signIn(
        email: String,
        password: String
    ): String {
        return executeCatching {
            val user = firebaseAuth.signInWithEmailAndPassword(email, password).user
            user?.getIdToken(true).orEmpty()
        }
    }

    actual suspend fun signUp(
        email: String,
        password: String
    ): String {
        return executeCatching {
            val user = firebaseAuth.createUserWithEmailAndPassword(email, password).user
            user?.getIdToken(true).orEmpty()
        }
    }

    actual suspend fun signOut() {
        firebaseAuth.signOut()
    }

    actual suspend fun signIn(authCredential: AuthCredential): String {
        return executeCatching {
            val user = firebaseAuth.signInWithCredential(authCredential).user
            user?.getIdToken(true).orEmpty()
        }
    }

    actual suspend fun signIn(token: String): String {
        return executeCatching {
            val user = firebaseAuth.signInWithCustomToken(token).user
            user?.getIdToken(true).orEmpty()
        }
    }

    actual fun isTokenExist(): Boolean = firebaseAuth.currentUser != null

    actual suspend fun signIn(): String {
        return executeCatching {
            firebaseAuth.currentUser?.getIdToken(true).orEmpty()
        }
    }

    actual suspend fun signInWithEmailLink(
        email: String,
        link: String
    ): String {
        return executeCatching {
            val user = firebaseAuth.signInWithEmailLink(email, link)
            user.user?.getIdToken(true).orEmpty()
        }
    }

    actual suspend fun sendSignInLinkToEmail(email: String, settings: ActionCodeSettings) {
        firebaseAuth.sendSignInLinkToEmail(email, settings)
    }
}