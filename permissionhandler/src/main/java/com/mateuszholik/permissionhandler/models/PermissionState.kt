package com.mateuszholik.permissionhandler.models

/**
 * State of the permission. It can have four states:
 * [AskForPermission], [ShowRationale], [Denied], [Granted]
 */
sealed class PermissionState {

    /**
     * Permission was not asked before or ask every time was selected.
     */
    data object AskForPermission : PermissionState()

    /**
     * Permission was denied once. Rationale should be displayed to the user.
     */
    data object ShowRationale : PermissionState()

    /**
     * Permission was denied forever. It can be granted only from system settings.
     */
    data object Denied : PermissionState()

    /**
     * Permission granted.
     */
    data object Granted : PermissionState()
}
