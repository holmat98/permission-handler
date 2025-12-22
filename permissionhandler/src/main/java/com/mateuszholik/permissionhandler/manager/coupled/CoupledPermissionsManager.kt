package com.mateuszholik.permissionhandler.manager.coupled

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

internal class CoupledPermissionsManager(
    private val permission: Permission.Coupled,
    private val permissionsPreferenceAssistant: PermissionsPreferenceAssistant,
    private val activity: Activity,
) : PermissionManager {

    private val states: MutableMap<String, State> by lazy {
        permission.names
            .associateWith {
                StateUtils.getInitialStateFor(
                    permissionName = it,
                    currentState = permissionsPreferenceAssistant.getState(it),
                    activity = activity,
                )
            }
            .toMutableMap()
    }

    override val initialState: PermissionState by lazy {
        val maxSdk = permission.maxSdk
        val minSdk = permission.minSdk

        when {
            (maxSdk != null && SdkProvider.provide() > maxSdk) ||
                    (minSdk != null && SdkProvider.provide() < minSdk) -> PermissionState.Granted

            getIsPartiallyGranted() -> PermissionState.PartiallyGranted
            states.containsValue(State.NOT_ASKED) -> PermissionState.AskForPermission
            states.containsValue(State.SHOW_RATIONALE) -> PermissionState.ShowRationale
            states.containsValue(State.DENIED) -> PermissionState.Denied
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

    override fun getPermissionsToAsk(): Array<String> =
        permission.names.toTypedArray()

    override fun handlePermissionResult(result: Map<String, Boolean>): PermissionState {
        result.forEach { (permissionName, isGranted) ->
            states[permissionName]?.let { currentState ->
                val nextState = currentState.getNextState(
                    permissionName = permissionName,
                    isGranted = isGranted,
                    activity = activity,
                )
                permissionsPreferenceAssistant.saveState(permissionName, nextState)
                states[permissionName] = nextState
            }
        }

        return when {
            getIsPartiallyGranted() -> PermissionState.PartiallyGranted
            states.containsValue(State.NOT_ASKED) -> PermissionState.AskForPermission
            states.containsValue(State.SHOW_RATIONALE) -> PermissionState.ShowRationale
            states.containsValue(State.DENIED) -> PermissionState.Denied
            else -> PermissionState.Granted
        }
    }

    override fun handleBackFromSettings(): PermissionState {
        states.forEach { (permissionName, state) ->
            val nextState = state.getNextState(
                permissionName = permissionName,
                isGranted = activity.isPermissionGranted(permissionName),
                activity = activity,
            )
            if (state != nextState) {
                permissionsPreferenceAssistant.saveState(permissionName, nextState)
                states[permissionName] = nextState
            }
        }

        return when {
            states.containsValue(State.SHOW_RATIONALE) -> PermissionState.ShowRationale
            states.containsValue(State.DENIED) -> PermissionState.Denied
            else -> PermissionState.Granted
        }
    }

    private fun getIsPartiallyGranted(): Boolean {
        if (permission.mainPermissions.isNullOrEmpty()) {
            return false
        }

        val areNotRequiredGranted = permission.names
            .filterNot { it in permission.mainPermissions }
            .all { states[it] == State.GRANTED }

        val areMainNotGranted = permission.mainPermissions
            .all { states[it] != State.GRANTED }

        return areNotRequiredGranted && areMainNotGranted
    }
}
