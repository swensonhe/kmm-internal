package com.swensonhe.strapikmm.util
import com.swensonhe.strapikmm.BuildConfig

actual class BuildConfig {
    actual fun isDebug() = BuildConfig.DEBUG
    actual fun isAndroid() = true
}