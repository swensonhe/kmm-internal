package com.swensonhe.strapikmm.errorhandling

import com.swensonhe.strapikmm.errorhandling.errortype.UnAuthorizedException
import com.swensonhe.strapikmm.errorhandling.errortype.UnexpectedException

class NetworkErrorMapper {

    fun mapThrowable(throwable: Throwable): AppException {
        return UnexpectedException(
            code = UNEXPECTED,
            message = "$throwable",
            throwable = throwable
        )
    }

    fun mapServerError(
        errorCode: Int?,
        errorMessage: String? = null,
        errorBody: String? = null,
        throwable: Throwable
    ): AppException {

        // TODO Handle more errors and timeout - no connection ...etc
        return when (errorCode) {
            UNAUTHORIZED -> UnAuthorizedException(
                code = errorCode,
                message = errorMessage
                    ?: "The application has encountered an unknown error",
                throwable = throwable
            )

            else -> AppException(
                errorCode = errorCode ?: UNEXPECTED,
                errorMessage = errorMessage
                    ?: "The application has encountered an unknown error",
                errorBody = errorBody,
                throwable = throwable
            )
        }
    }

    companion object {
        private const val TIME_OUT = -100
        private const val NO_CONNECTION = -101
        private const val UNEXPECTED = -102
        private const val UNAUTHORIZED = 30001
    }
}
