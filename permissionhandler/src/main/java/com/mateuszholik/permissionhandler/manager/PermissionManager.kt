package com.mateuszholik.permissionhandler.manager

import android.app.Activity
import com.mateuszholik.permissionhandler.extensions.isPermissionGranted
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.State
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant

internal interface PermissionManager {

    val initialState: PermissionState

    fun handlePermissionResult(result: Map<String, Boolean>): PermissionState

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
    permission: Permission,
    private val permissionsPreferenceAssistant: PermissionsPreferenceAssistant,
    private val activity: Activity,
) : PermissionManager {

    private val states: MutableMap<String, State> =
        permission.permissions
            .associateWith { getInitialStateFor(it) }
            .toMutableMap()

    override val initialState: PermissionState =
        when {
            SdkProvider.provide() < permission.minSdk -> PermissionState.Granted
            states.containsValue(State.NOT_ASKED) -> PermissionState.AskForPermission
            states.containsValue(State.SHOW_RATIONALE) -> PermissionState.ShowRationale
            states.containsValue(State.DENIED) -> PermissionState.Denied
            else -> PermissionState.Granted
        }

    override fun handlePermissionResult(result: Map<String, Boolean>): PermissionState {
        result.forEach { (permissionName, isGranted) ->
            states[permissionName]?.let { currentState ->
                val nextState = currentState.getNextState(permissionName, isGranted)
                permissionsPreferenceAssistant.saveState(permissionName, nextState)
                states[permissionName] = nextState
            }
        }

        return when {
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


    private fun getInitialStateFor(permissionName: String): State =
        when (permissionsPreferenceAssistant.getState(permissionName)) {
            State.NOT_ASKED -> State.NOT_ASKED
            State.SHOW_RATIONALE -> {
                if (activity.shouldShowRequestPermissionRationale(permissionName)) {
                    State.SHOW_RATIONALE
                } else {
                    State.DENIED
                }
            }
            State.DENIED -> State.DENIED
            State.GRANTED -> {
                if (activity.isPermissionGranted(permissionName)) {
                    State.GRANTED
                } else {
                    State.NOT_ASKED
                }
            }
        }

    private fun State.getNextState(permissionName: String, isGranted: Boolean): State =
        when {
            isGranted -> State.GRANTED
            this == State.NOT_ASKED || activity.shouldShowRequestPermissionRationale(permissionName) -> State.SHOW_RATIONALE
            else -> State.DENIED
        }
}
