package com.mateuszholik.permissionhandler.manager

import android.app.Activity
import com.mateuszholik.permissionhandler.extensions.isGranted
import com.mateuszholik.permissionhandler.extensions.shouldShowRationale
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.State
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant

interface PermissionManager {

    val initialState : PermissionState

    fun handlePermissionResult(isGranted: Boolean): PermissionState

    fun handleBackFromSettings(): PermissionState

    companion object {
        fun newInstance(
            activity: Activity,
            permission: Permission,
        ): PermissionManager =
            PermissionManagerImpl(
                activity = activity,
                permission = permission,
                permissionsPreferenceAssistant = PermissionsPreferenceAssistant.newInstance(activity.applicationContext),
            )
    }
}

internal class PermissionManagerImpl(
    private val permission: Permission,
    private val permissionsPreferenceAssistant: PermissionsPreferenceAssistant,
    private val activity: Activity,
) : PermissionManager {

    private var state: State = permissionsPreferenceAssistant.getState(permission.name)
        set(value) {
            permissionsPreferenceAssistant.saveState(permission.name, value)
            field = value
        }

    override val initialState: PermissionState =
        if (SdkProvider.provide() < permission.minSdk) {
            PermissionState.Granted
        } else {
            when (state) {
                State.NOT_ASKED -> PermissionState.AskForPermission
                State.SHOW_RATIONALE -> {
                    if (activity.shouldShowRationale(permission)) {
                        PermissionState.ShowRationale
                    } else {
                        state = State.DENIED
                        PermissionState.Denied
                    }
                }
                State.DENIED -> PermissionState.Denied
                State.GRANTED -> {
                    if (permission.isGranted(activity)) {
                        PermissionState.Granted
                    } else {
                        PermissionState.ShowRationale
                    }
                }
            }
        }

    override fun handlePermissionResult(isGranted: Boolean): PermissionState {
        state = when {
            isGranted -> State.GRANTED
            state == State.NOT_ASKED && activity.shouldShowRationale(permission) -> State.SHOW_RATIONALE
            else -> State.DENIED
        }

        return when (state) {
            State.GRANTED -> PermissionState.Granted
            State.SHOW_RATIONALE -> PermissionState.ShowRationale
            State.DENIED -> PermissionState.Denied
            State.NOT_ASKED -> PermissionState.AskForPermission
        }
    }

    override fun handleBackFromSettings(): PermissionState =
        when {
            permission.isGranted(activity) -> {
                state = State.GRANTED
                PermissionState.Granted
            }
            activity.shouldShowRationale(permission) -> {
                state = State.SHOW_RATIONALE
                PermissionState.ShowRationale
            }
            else -> PermissionState.Denied
        }
}
