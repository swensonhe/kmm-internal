package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PagingResponse<T>(
    @SerialName("data")
    val data: List<T>?,
    @SerialName("meta.pagination.page")
    val page: Int?,
    @SerialName("meta.pagination.pageSize")
    val pageSize: Int?,
    @SerialName("meta.pagination.pageCount")
    val pageCount: Int?,
    @SerialName("meta.pagination.total")
    val total: Int?
)
