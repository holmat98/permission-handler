package com.mateuszholik.permissionhandler.manager

import android.app.Activity
import android.content.res.TypedArray
import com.mateuszholik.permissionhandler.manager.coupled.CoupledPermissionsManager
import com.mateuszholik.permissionhandler.manager.single.SinglePermissionManager
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant

internal interface PermissionManager {

    val initialState: PermissionState

    fun getPermissionsToAsk(): Array<String>

    fun handlePermissionResult(result: Map<String, Boolean>): PermissionState

    fun handleBackFromSettings(): PermissionState

    companion object {
        fun newInstance(
            activity: Activity,
            permission: Permission,
        ): PermissionManager =
            when (permission) {
                is Permission.Coupled -> {
                    CoupledPermissionsManager(
                        activity = activity,
                        permission = permission,
                        permissionsPreferenceAssistant =
                            PermissionsPreferenceAssistant.newInstance(activity.applicationContext),
                    )
                }

                is Permission.Single -> {
                    SinglePermissionManager(
                        activity = activity,
                        permission = permission,
                        permissionsPreferenceAssistant =
                            PermissionsPreferenceAssistant.newInstance(activity.applicationContext),
                    )
                }
            }
    }
}
