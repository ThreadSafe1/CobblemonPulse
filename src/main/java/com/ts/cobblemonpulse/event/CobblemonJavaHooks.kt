@file:JvmName("CobblemonJavaHooks")

package com.ts.cobblemonpulse.event

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.Observable
import java.util.function.Consumer

fun <T> subscribeJava(
    observable: Observable<T>,
    priority: Priority,
    handler: Consumer<T>
) {
    observable.subscribe(priority) {
        handler.accept(it)
    }
}