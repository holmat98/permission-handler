package com.mateuszholik.permissionhandler.manager.single

import android.app.Activity
import com.mateuszholik.permissionhandler.extensions.isPermissionGranted
import com.mateuszholik.permissionhandler.manager.PermissionManager
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.State
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant
import com.mateuszholik.permissionhandler.utils.StateUtils
import com.mateuszholik.permissionhandler.utils.StateUtils.getNextState

internal class SinglePermissionManager(
    private val permission: Permission.Single,
    private val permissionsPreferenceAssistant: PermissionsPreferenceAssistant,
    private val activity: Activity,
) : PermissionManager {

    private var state: State = StateUtils.getInitialStateFor(
        permissionName = permission.name,
        currentState = permissionsPreferenceAssistant.getState(permission.name),
        activity = activity,
    )

    override val initialState: PermissionState by lazy {
        val maxSdk = permission.maxSdk
        val minSdk = permission.minSdk
        when {
            (maxSdk != null && SdkProvider.provide() > maxSdk) ||
                    (minSdk != null && SdkProvider.provide() < minSdk) -> PermissionState.Granted

            state == State.NOT_ASKED -> PermissionState.AskForPermission
            state == State.SHOW_RATIONALE -> PermissionState.ShowRationale
            state == State.DENIED -> PermissionState.Denied
            else -> PermissionState.Granted
        }
    }

    init {
        val maxSdk = permission.maxSdk
        val minSdk = permission.minSdk
        if (maxSdk != null && minSdk != null && maxSdk < minSdk) {
            error("MaxSdk (${permission.maxSdk}) have to be greater or equal to minSdk (${permission.minSdk}).")
        }
    }

    override fun handlePermissionResult(result: Map<String, Boolean>): PermissionState {
        result.forEach { (permissionName, isGranted) ->
            val nextState = state.getNextState(
                permissionName = permissionName,
                isGranted = isGranted,
                activity = activity,
            )
            permissionsPreferenceAssistant.saveState(permissionName, nextState)
            state = nextState
        }

        return when (state) {
            State.NOT_ASKED -> PermissionState.AskForPermission
            State.SHOW_RATIONALE -> PermissionState.ShowRationale
            State.DENIED -> PermissionState.Denied
            else -> PermissionState.Granted
        }
    }

    override fun handleBackFromSettings(): PermissionState {
        val nextState = state.getNextState(
            permissionName = permission.name,
            isGranted = activity.isPermissionGranted(permission.name),
            activity = activity,
        )
        if (state != nextState) {
            permissionsPreferenceAssistant.saveState(permission.name, nextState)
            state = nextState
        }

        return when (state) {
            State.SHOW_RATIONALE -> PermissionState.ShowRationale
            State.DENIED -> PermissionState.Denied
            else -> PermissionState.Granted
        }
    }
}
