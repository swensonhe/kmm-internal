package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.errorhandling.AppException

data class DataState<T>(
    val error: AppException? = null,
    val data: T? = null,
    val isLoading: Boolean = false,
) {

    companion object {

        fun <T> error(
            error: AppException,
        ): DataState<T> {
            return DataState(
                error = error,
                data = null,
            )
        }

        fun <T> data(
            error: AppException? = null,
            data: T? = null,
        ): DataState<T> {
            return DataState(
                error = error,
                data = data,
            )
        }

        fun <T>loading() = DataState<T>(isLoading = true)
    }
}
