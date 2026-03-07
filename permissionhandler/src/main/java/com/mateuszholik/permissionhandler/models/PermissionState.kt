package com.mateuszholik.permissionhandler.models

/**
 * State of the permission. It can have five states:
 * [AskForPermission], [ShowRationale], [Denied], [Granted]
 */
sealed interface PermissionState {

    /**
     * Permission was not asked before or ask every time was selected.
     */
    data object AskForPermission : PermissionState

    /**
     * Permission was denied once. Rationale should be displayed to the user.
     */
    data object ShowRationale : PermissionState

    /**
     * Permission was denied forever. It can be granted only from system settings.
     */
    data object Denied : PermissionState

    /**
     * Permission granted.
     *
     * @param isPartiallyGranted set to true when for example READ_MEDIA_VISUAL_USER_SELECTED is selected by user
     */
    data class Granted(val isPartiallyGranted: Boolean = false) : PermissionState
}
