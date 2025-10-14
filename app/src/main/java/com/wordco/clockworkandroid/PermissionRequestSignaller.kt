package com.wordco.clockworkandroid

import kotlinx.coroutines.flow.SharedFlow

interface PermissionRequestSignaller {
    val stream: SharedFlow<String>
    suspend fun request(permission: String)
}