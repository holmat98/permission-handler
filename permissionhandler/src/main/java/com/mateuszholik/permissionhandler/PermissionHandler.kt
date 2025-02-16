package com.mateuszholik.permissionhandler

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.mateuszholik.permissionhandler.extensions.activity
import com.mateuszholik.permissionhandler.extensions.permissions
import com.mateuszholik.permissionhandler.manager.PermissionManager
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState

/**
 * Holder of the state of the permission and lambda to launch permission system dialog.
 *
 * @property currentPermissionState current permission state
 * @property launchPermissionDialog launches permission dialog or system settings
 */
@ConsistentCopyVisibility
@Immutable
data class PermissionHandler internal constructor(
    val currentPermissionState: PermissionState,
    val launchPermissionDialog: () -> Unit,
)

/**
 * Provides current [PermissionState] of the permission. Launches the permission system dialog
 * or navigates to the system settings when current state is equal to [Denied][com.mateuszholik.permissionhandler.models.PermissionState.Denied].
 *
 * @param permission permission for which the flow is handled
 * @return [PermissionHandler]
 */
@Composable
fun rememberPermissionHandler(permission: Permission): State<PermissionHandler> {
    if (LocalInspectionMode.current) {
        return remember {
            mutableStateOf(
                PermissionHandler(
                    currentPermissionState = PermissionState.Granted,
                    launchPermissionDialog = {}
                )
            )
        }
    } else {
        val activity = LocalContext.current.activity

        val permissionManager = remember {
            PermissionManager.newInstance(
                activity = activity,
                permission = permission,
            )
        }

        var state by remember { mutableStateOf(permissionManager.initialState) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            state = permissionManager.handlePermissionResult(result)
        }

        val settingsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { state = permissionManager.handleBackFromSettings() }

        return remember {
            derivedStateOf {
                PermissionHandler(
                    currentPermissionState = state,
                    launchPermissionDialog = {
                        when (state) {
                            PermissionState.AskForPermission,
                            PermissionState.ShowRationale -> {
                                permissionLauncher.launch(permission.permissions.toTypedArray())
                            }

                            PermissionState.Denied -> {
                                settingsLauncher.launch(
                                    Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts(
                                            "package",
                                            activity.applicationContext.packageName,
                                            null
                                        )
                                    )
                                )
                            }

                            PermissionState.Granted -> Unit
                        }
                    }
                )
            }
        }
    }
}
