package com.wordco.clockworkandroid

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow

interface PermissionRequestSignaller {
    val requestStream: Flow<PermissionRequest>
    suspend fun request(permission: String) : Boolean
}

data class PermissionRequest(
    val permission: String,
    val result: CompletableDeferred<Boolean>
)