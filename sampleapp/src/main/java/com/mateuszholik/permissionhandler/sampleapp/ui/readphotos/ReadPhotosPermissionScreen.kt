package com.mateuszholik.permissionhandler.sampleapp.ui.readphotos

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
fun ReadPhotosPermissionScreen(
    onBackPressed: () -> Unit,
) {
    val permissionHandler by rememberPermissionHandler(
        permission = when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> {
                Permission.Single(
                    name = Manifest.permission.READ_EXTERNAL_STORAGE,
                    maxSdk = Build.VERSION_CODES.S_V2,
                )
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
                Permission.Single(
                    name = Manifest.permission.READ_MEDIA_IMAGES,
                    minSdk = Build.VERSION_CODES.TIRAMISU,
                )
            }
            else -> {
                Permission.Coupled(
                    names = listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    ),
                    minSdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                )
            }
        }
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
