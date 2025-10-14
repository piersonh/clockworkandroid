package com.wordco.clockworkandroid.user_stats_feature.ui.util

sealed interface Result <out S : Any, out E : Any> {
    data class Success<S : Any>(val result: S) : Result<S, Nothing>
    data class Error<E : Any>(val error: E) : Result<Nothing,E>

    fun takeIfError() : E? {
        return when (this) {
            is Success<S> -> null
            is Error<E> -> this.error
        }
    }

    fun takeIfSuccess() : S? {
        return when (this) {
            is Success<S> -> this.result
            is Error<E> -> null
        }
    }
}