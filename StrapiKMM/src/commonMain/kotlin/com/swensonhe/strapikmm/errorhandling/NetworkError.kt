package com.swensonhe.strapikmm.errorhandling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(
    @SerialName("error.status")
    val code: Int,
    @SerialName("error.message")
    val message: String?
)
