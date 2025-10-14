package com.wordco.clockworkandroid.core.domain.model

import kotlinx.coroutines.CompletableDeferred

data class PermissionRequest(
    val permission: String,
    val result: CompletableDeferred<Boolean>
)