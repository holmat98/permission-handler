package com.mateuszholik.permissionhandler.sampleapp.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.sampleapp.BuildConfig
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.scaffold.CommonScaffold
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.CommonText
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.HeaderText

@Composable
fun MainScreen(
    onCameraPermissionPressed: () -> Unit,
    onLocationPermissionPressed: () -> Unit,
    onNotificationPermissionPressed: () -> Unit,
    onInfoPressed: () -> Unit,
) {
    CommonScaffold(
        actions = {
            CommonIconButton(
                icon = Icons.Outlined.Info,
                onClick = onInfoPressed,
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.padding(bottom = 16.dp),
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
                HeaderText(text = stringResource(R.string.app_name))
                CommonText(
                    text = BuildConfig.VERSION_NAME,
                    fontWeight = FontWeight.Bold,
                )
            }

            CommonButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textResId = R.string.permission_camera,
                onClick = onCameraPermissionPressed,
            )
            CommonButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textResId = R.string.permission_location,
                onClick = onLocationPermissionPressed,
            )
            CommonButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textResId = R.string.permission_notification,
                onClick = onNotificationPermissionPressed,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PermissionHandlerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            MainScreen(
                onCameraPermissionPressed = {},
                onLocationPermissionPressed = {},
                onNotificationPermissionPressed = {},
                onInfoPressed = {},
            )
        }
    }
}
