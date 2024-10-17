package com.mateuszholik.permissionhandler.sampleapp.ui.writeexternal

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.rememberPermissionHandler
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.permission.PermissionContent
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.scaffold.CommonScaffold

@Composable
fun WriteExternalStoragePermissionScreen(
    onBackPressed: () -> Unit,
) {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission.Single(
            name = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            maxSdk = Build.VERSION_CODES.P,
        )
    )

    CommonScaffold(
        navigationIcon = {
            CommonIconButton(icon = Icons.Default.ArrowBack, onClick = onBackPressed)
        }
    ) {
        PermissionContent(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            permissionName = stringResource(R.string.permission_write_external_storage).uppercase(),
            permissionIconDrawable = R.drawable.ic_write_external_storage,
            permissionState = permissionHandler.currentPermissionState,
            onButtonPressed = permissionHandler.launchPermissionDialog
        )
    }
}
