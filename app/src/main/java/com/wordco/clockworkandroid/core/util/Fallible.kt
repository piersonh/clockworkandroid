package com.wordco.clockworkandroid.core.util

sealed interface Fallible <out T : Any> {
    data object Success : Fallible<Nothing>
    data class Error<T : Any>(val error: T) : Fallible<T>

    fun takeIfError() : T? {
        return when (this) {
            is Error<T> -> this.error
            Success -> null
        }
    }
}