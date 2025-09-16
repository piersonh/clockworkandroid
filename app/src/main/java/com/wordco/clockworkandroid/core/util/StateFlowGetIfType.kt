package com.wordco.clockworkandroid.core.util

import kotlinx.coroutines.flow.MutableStateFlow

inline fun <reified T> MutableStateFlow<*>.getIfType() : T? {
    return value as? T
}