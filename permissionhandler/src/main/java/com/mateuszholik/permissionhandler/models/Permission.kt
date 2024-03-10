package com.mateuszholik.permissionhandler.models

import android.os.Build

/**
 * Holder of the base information's of the permission. It can be either [Single] or [Coupled].
 */
sealed interface Permission {

    /**
     * Minimum sdk version that supports this permission. For example for POST_NOTIFICATIONS permission
     * it will be equal to 33 (Android 13).
     */
    val minSdk: Int

    /**
     * Should be used for base permissions that are requested alone, for example CAMERA permission.
     *
     * @property name permission name from [android.Manifest.permission]
     * @property minSdk minimum sdk version for this permission. Default value is equal to 26 (Android 8)
     */
    data class Single(
        val name: String,
        override val minSdk: Int = Build.VERSION_CODES.O,
    ) : Permission

    /**
     * Should be used for permission that have to be requested all together, for example:
     * ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION
     *
     * @property names list of permissions names from [android.Manifest.permission]
     * @property minSdk minimum sdk version for this permission. Default value is equal to 26 (Android 8)
     */
    data class Coupled(
        val names: List<String>,
        override val minSdk: Int = Build.VERSION_CODES.O,
    ) : Permission
}
