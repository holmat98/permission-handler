package com.mateuszholik.permissionhandler

import android.content.Context
import android.os.Build
import com.mateuszholik.permissionhandler.extensions.isGranted
import com.mateuszholik.permissionhandler.listeners.OnStateChangedListener
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SinglePermissionHandler {

    fun observe(): Flow<SinglePermissionState>

    /**
     * This method handles the result from system permission dialog
     *
     * @throws IllegalStateException when called if current state is [Granted][SinglePermissionState.Granted] or [Denied][SinglePermissionState.Denied]
     */
    fun handlePermissionResult(isGranted: Boolean)

    /**
     * This method handles the permission state when user comes back from settings
     *
     * @throws IllegalStateException when called if current state is not [Denied][SinglePermissionState.Denied]
     */
    fun handleBackFromSettings()

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
            init: Builder.() -> Unit,
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
        set(value) {
            permissionsPreferenceAssistant.savePermissionState(permission.name, value)
            field = value
        }

    private var currentState: SinglePermissionState? = null
    private var listener: OnStateChangedListener<SinglePermissionState>? = null

    override fun observe(): Flow<SinglePermissionState> =
        callbackFlow {
            listener = OnStateChangedListener {
                trySend(it)
                currentState = it
            }

            listener?.onStateChanged(getInitialState())

            awaitClose {
                listener = null
                currentState = null
            }
        }

    override fun handlePermissionResult(isGranted: Boolean) {
        if (currentState is SinglePermissionState.Granted || currentState is SinglePermissionState.Denied) {
            error("Illegal state - called when permission state is $currentState")
        }

        currentPermissionState =
            currentPermissionState.nextPermissionState(isGranted, permission.isOptional)

        val nextState = when (currentPermissionState) {
            PermissionState.GRANTED -> SinglePermissionState.Granted
            PermissionState.SKIPPED -> SinglePermissionState.Skipped
            PermissionState.DENIED -> SinglePermissionState.Denied
            PermissionState.SHOW_RATIONALE -> SinglePermissionState.ShowRationale(permission.name)
            PermissionState.NOT_ASKED -> SinglePermissionState.AskForPermission(permission.name)
        }

        listener?.onStateChanged(nextState)
    }

    override fun handleBackFromSettings() {
        if (currentState !is SinglePermissionState.Denied) {
            error("Illegal state - called when permission state is $currentState")
        }

        if (permission.isGranted(context)) {
            currentPermissionState = PermissionState.GRANTED

            listener?.onStateChanged(SinglePermissionState.Granted)
        } else {
            listener?.onStateChanged(SinglePermissionState.Denied)
        }
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
