package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ItemResponse<T>(
    @SerialName("data")
    val data: T
)