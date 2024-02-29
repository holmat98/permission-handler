package com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.rememberPermissionHandler
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.permission.PermissionContent

@Composable
fun SinglePermissionScreen() {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission(
            name = Manifest.permission.CAMERA,
            minSdk = 26
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        PermissionContent(
            modifier = Modifier
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp,
                )
                .fillMaxSize(),
            permissionName = "CAMERA",
            permissionIconDrawable = R.drawable.ic_camera,
            permissionState = permissionHandler.currentPermissionState,
            onButtonPressed = permissionHandler.launchPermissionDialog
        )
    }
}
