package com.mateuszholik.permissionhandler.sampleapp.ui.camera

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.rememberPermissionHandler
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.permission.PermissionContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPermissionScreen(
    onBackPressed: () -> Unit,
) {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission(
            name = Manifest.permission.CAMERA,
            minSdk = 26
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
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
            permissionName = stringResource(R.string.permission_camera).uppercase(),
            permissionIconDrawable = R.drawable.ic_camera,
            permissionState = permissionHandler.currentPermissionState,
            onButtonPressed = permissionHandler.launchPermissionDialog
        )
    }
}
