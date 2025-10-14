package com.wordco.clockworkandroid.core.data.permission

import com.wordco.clockworkandroid.core.domain.model.PermissionRequest
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class PermissionRequestSignal : PermissionRequestSignaller {
    private val _requestChannel = Channel<PermissionRequest>()
    override val requestStream = _requestChannel.receiveAsFlow()

    /**
     * The component calls this function. It suspends until the result is available.
     * @return True if the permission was granted, false otherwise.
     */
    override suspend fun request(permission: String): Boolean {
        val request = PermissionRequest(
            permission = permission,
            result = CompletableDeferred()
        )
        _requestChannel.send(request)
        return request.result.await() // Suspends here until the result is set
    }
}