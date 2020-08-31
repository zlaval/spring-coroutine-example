package com.zlrx.springcoroutine.demo.service

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext

val dispatcher = newFixedThreadPoolContext(3, "CarAssembleDispatcher") + CoroutineName("CAR ASSEMBLE")

inline fun <T> async(crossinline block: suspend CoroutineScope.() -> T): Deferred<T> =
    GlobalScope.async(dispatcher) {
        block()
    }
