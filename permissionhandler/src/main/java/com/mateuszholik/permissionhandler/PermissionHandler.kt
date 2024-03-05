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
import com.mateuszholik.permissionhandler.extensions.activity
import com.mateuszholik.permissionhandler.manager.PermissionManager
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState

@Immutable
data class PermissionHandler(
    val currentPermissionState: PermissionState,
    val launchPermissionDialog: () -> Unit,
)

@Composable
fun rememberPermissionHandler(permission: Permission): State<PermissionHandler> {
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
