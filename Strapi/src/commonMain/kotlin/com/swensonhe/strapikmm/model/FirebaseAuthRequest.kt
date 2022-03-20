package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?
)
