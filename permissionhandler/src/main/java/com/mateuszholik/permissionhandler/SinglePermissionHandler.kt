package com.mateuszholik.permissionhandler

import android.content.Context
import android.os.Build
import com.mateuszholik.permissionhandler.extensions.isGranted
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SinglePermissionHandler {

    val state: StateFlow<SinglePermissionState>

    /**
     * This method handles the result from system permission dialog
     *
     * @throws IllegalStateException when called if current state is [Granted][SinglePermissionState.Granted] or [Denied][SinglePermissionState.Denied]
     */
    suspend fun handlePermissionResult(isGranted: Boolean)

    /**
     * This method handles the permission state when user comes back from settings
     *
     * @throws IllegalStateException when called if current state is not [Denied][SinglePermissionState.Denied]
     */
    suspend fun handleBackFromSettings()

    class Builder internal constructor(private val context: Context) {

        /**
         * Permission
         */
        lateinit var permission: String

        /**
         * Indicates if the permission is required by the application to function correctly.
         * Default value is set to false
         */
        var isOptional: Boolean = false

        /**
         * Minimal Android version for this permission.
         * For example android.permission.POST_NOTIFICATIONS should have minSdk value set to 33.
         * Default value is set to 1.
         */
        var minSdk: Int = Build.VERSION_CODES.ICE_CREAM_SANDWICH

        /**
         * Returns the instance of the SinglePermissionHandler.
         *
         * @throws IllegalStateException when permission is not provided.
         */
        fun build(): SinglePermissionHandler {
            if (!::permission.isInitialized) {
                error("Permission was not provided")
            }

            return SinglePermissionHandlerImpl(
                permission = Permission(
                    name = permission,
                    isOptional = isOptional,
                    minSdk = minSdk
                ),
                permissionsPreferenceAssistant = PermissionsPreferenceAssistant.newInstance(context),
                context = context,
            )
        }
    }

    companion object {
        fun builder(
            context: Context,
            init: Builder.() -> Unit
        ): SinglePermissionHandler =
            Builder(context).apply(init).build()
    }
}

internal class SinglePermissionHandlerImpl(
    private val permission: Permission,
    private val permissionsPreferenceAssistant: PermissionsPreferenceAssistant,
    private val context: Context,
) : SinglePermissionHandler {

    private var currentPermissionState: PermissionState =
        permissionsPreferenceAssistant.getPermissionState(permission.name)

    private val _state: MutableStateFlow<SinglePermissionState> =
        MutableStateFlow(getInitialState())
    override val state: StateFlow<SinglePermissionState>
        get() = _state.asStateFlow()

    override suspend fun handlePermissionResult(isGranted: Boolean) {
        val currentState = _state.value
        if (currentState is SinglePermissionState.Granted || currentState is SinglePermissionState.Denied) {
            error("Illegal state - called when permission state is $currentState")
        }

        val nextState = currentPermissionState.nextPermissionState(isGranted, permission.isOptional)
        currentPermissionState = nextState
        permissionsPreferenceAssistant.savePermissionState(
            permissionName = permission.name,
            state = nextState
        )

        _state.emit(getNextSinglePermissionState())
    }

    override suspend fun handleBackFromSettings() {
        val currentState = _state.value
        if (currentState !is SinglePermissionState.Denied) {
            error("Illegal state - called when permission state is $currentState")
        }

        if (permission.isGranted(context)) {
            currentPermissionState = PermissionState.GRANTED
            permissionsPreferenceAssistant.savePermissionState(
                permissionName = permission.name,
                state = PermissionState.GRANTED
            )

            _state.emit(SinglePermissionState.Granted)
        }
    }

    private fun getNextSinglePermissionState(): SinglePermissionState =
        when (currentPermissionState) {
            PermissionState.GRANTED -> SinglePermissionState.Granted
            PermissionState.SKIPPED,
            PermissionState.DENIED -> SinglePermissionState.Denied
            PermissionState.SHOW_RATIONALE -> SinglePermissionState.ShowRationale(permission.name)
            PermissionState.NOT_ASKED -> SinglePermissionState.AskForPermission(permission.name)
        }

    private fun getInitialState(): SinglePermissionState {
        if (SdkProvider.provide() < permission.minSdk) {
            return SinglePermissionState.Granted
        }

        return when (currentPermissionState) {
            PermissionState.NOT_ASKED -> SinglePermissionState.AskForPermission(permission.name)
            PermissionState.SKIPPED,
            PermissionState.SHOW_RATIONALE -> SinglePermissionState.ShowRationale(permission.name)
            PermissionState.DENIED -> SinglePermissionState.Denied
            PermissionState.GRANTED -> {
                if (permission.isGranted(context)) {
                    SinglePermissionState.Granted
                } else {
                    permissionsPreferenceAssistant.savePermissionState(
                        permissionName = permission.name,
                        state = PermissionState.SHOW_RATIONALE
                    )
                    SinglePermissionState.ShowRationale(permission.name)
                }
            }
        }
    }

    private fun PermissionState.nextPermissionState(
        isGranted: Boolean,
        isOptional: Boolean,
    ): PermissionState =
        when {
            isGranted -> PermissionState.GRANTED
            this == PermissionState.NOT_ASKED && isOptional -> PermissionState.SKIPPED
            this == PermissionState.NOT_ASKED -> PermissionState.SHOW_RATIONALE
            else -> PermissionState.DENIED
        }

}
