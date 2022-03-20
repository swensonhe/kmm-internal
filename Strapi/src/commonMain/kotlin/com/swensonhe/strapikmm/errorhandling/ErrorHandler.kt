package com.swensonhe.strapikmm.errorhandling

import com.swensonhe.strapikmm.util.DataState
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> executeCatching(block: suspend () -> Unit, flow: FlowCollector<DataState<T>>) {
    try {
        block()
    } catch (throwable: Throwable) {
        handleError<T>(throwable, flow)
    }
}

suspend fun <T> handleError(throwable: Throwable, flow: FlowCollector<DataState<T>>) {
    val error = if (throwable is AppException) {
        throwable
    } else {
        NetworkErrorMapper().mapThrowable(throwable)
    }

    flow.emit(
        DataState.error(
            AppException(
                errorMessage = error.errorMessage,
                errorCode = error.errorCode
            )
        )
    )
}

fun handleError(throwable: Throwable): AppException {
    return if (throwable is AppException) {
        throwable
    } else {
        NetworkErrorMapper().mapThrowable(throwable)
    }
}


suspend fun <T> executeCatching(block: suspend () -> T): T {
    try {
        return block()
    } catch (throwable: Throwable) {
        throw handleError(throwable)
    }
}
