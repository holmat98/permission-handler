package com.mateuszholik.permissionhandler.models

sealed class PermissionState {

    data object AskForPermission : PermissionState()

    data object ShowRationale : PermissionState()

    data object Denied : PermissionState()

    data object Granted : PermissionState()
}
