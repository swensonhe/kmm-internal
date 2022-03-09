package com.swensonhe.strapikmm.errorhandling.errortype

import com.swensonhe.strapikmm.errorhandling.AppException

class NoConnectionException(code: Int, message: String) :
    AppException(errorCode = code, errorMessage = message)

class TimeOutException(code: Int, message: String) :
    AppException(errorCode = code, errorMessage = message)

class UnexpectedException(code: Int, message: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = message, throwable = throwable)

class UnAuthorizedException(code: Int, message: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = message, throwable = throwable)
