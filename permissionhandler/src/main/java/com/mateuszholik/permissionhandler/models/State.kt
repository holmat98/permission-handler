package com.mateuszholik.permissionhandler.models

sealed interface State

sealed class SinglePermissionState : State {

    data class AskForPermission(val permission: String) : SinglePermissionState()

    data class ShowRationale(val permission: String) : SinglePermissionState()

    data object Skipped : SinglePermissionState()

    data object Denied : SinglePermissionState()

    data object Granted : SinglePermissionState()
}

sealed class MultiplePermissionState : State {

    data class AskForPermissions(val permissions: List<String>) : MultiplePermissionState()

    data class ShowRationale(val permissions: List<String>) : MultiplePermissionState()

    data object Denied : MultiplePermissionState()

    data object Granted : MultiplePermissionState()
}
