package com.mateuszholik.permissionhandler.sampleapp.ui.readphotos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mateuszholik.permissionhandler.rememberPermissionHandler
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.permission.PermissionContent
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.scaffold.CommonScaffold
import com.mateuszholik.permissionhandler.utils.PermissionsConstants

@Composable
fun ReadPhotosPermissionScreen(
    onBackPressed: () -> Unit,
) {
    val permissionHandler by rememberPermissionHandler(
        permission = PermissionsConstants.READ_PHOTOS_PERMISSION,
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
            permissionName = stringResource(R.string.permission_read_images).uppercase(),
            permissionIconDrawable = R.drawable.ic_notification,
            permissionState = permissionHandler.currentPermissionState,
            onButtonPressed = permissionHandler.launchPermissionDialog
        )
    }
}
