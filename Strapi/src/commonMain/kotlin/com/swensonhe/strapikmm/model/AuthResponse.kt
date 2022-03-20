package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse<T>(
    @SerialName("user")
    val user: T,
    @SerialName("jwt")
    val jwt: String?,
)
