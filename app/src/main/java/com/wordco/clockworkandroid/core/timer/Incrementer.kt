package com.wordco.clockworkandroid.core.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive

internal fun interface Incrementer {
    operator fun invoke(): suspend CoroutineScope.() -> Unit

    companion object {
        fun of(
            interval: Long,
            initialOffset: () -> Long,
            stateField: MutableStateFlow<Int>,
        ) : Incrementer = Incrementer {
            runOnInterval(
                interval = interval,
                initialOffset = initialOffset
            ) {
                stateField.update { it + 1 }
            }
        }

        /**
         * Invokes the provided function [block] every [interval] milliseconds
         *
         * @param interval time in milliseconds between triggers
         * @param initialOffset time in milliseconds to offset first interval
         */
        private fun runOnInterval(
            interval: Long,
            initialOffset: () -> Long,
            block: () -> Unit,
        ): suspend CoroutineScope.() -> Unit {
            return {
                // Wait for next minute
                delay(interval - (initialOffset() % interval))

                // Start after synchronized with minute
                while (isActive) {
                    block()
                    delay(interval)
                }
            }
        }
    }
}