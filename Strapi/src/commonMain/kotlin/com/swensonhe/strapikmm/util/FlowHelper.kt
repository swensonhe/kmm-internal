package com.swensonhe.strapikmm.util

import com.kuuurt.paging.multiplatform.helpers.dispatcher
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

class CommonFlow<T>(private val origin: Flow<T>): Flow<T> by origin {
    fun collectCommon(
        coroutineScope: CoroutineScope? = null, // 'viewModelScope' on Android and 'nil' on iOS
        callback: (T) -> Unit, // callback on each emission
    ){
        onEach {
            callback(it)
        }.launchIn(coroutineScope ?: CoroutineScope(Dispatchers.Main))
    }
}

fun <T> Flow<T>.asPagingFlow(): PagingFlow<T> = PagingFlow(this)

class PagingFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()
        onEach { block(it) }.launchIn(CoroutineScope(job + dispatcher()))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}
