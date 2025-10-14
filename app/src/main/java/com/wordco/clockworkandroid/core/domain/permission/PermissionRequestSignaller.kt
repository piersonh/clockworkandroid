package com.wordco.clockworkandroid.core.domain.permission

import com.wordco.clockworkandroid.core.domain.model.PermissionRequest
import kotlinx.coroutines.flow.Flow

interface PermissionRequestSignaller {
    val requestStream: Flow<PermissionRequest>

    /**
     * The component calls this function. It suspends until the result is available.
     * @return True if the permission was granted, false otherwise.
     */
    suspend fun request(permission: String): Boolean
}