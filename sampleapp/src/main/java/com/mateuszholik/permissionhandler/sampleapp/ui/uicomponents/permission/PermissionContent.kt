package com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.permission

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons.CommonButton

@Composable
fun PermissionContent(
    permissionName: String,
    @DrawableRes permissionIconDrawable: Int,
    permissionState: PermissionState,
    onButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                painter = painterResource(permissionIconDrawable),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = permissionName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 28.sp,
                text = "Current state:",
            )
            Text(
                text = permissionState::class.java.simpleName,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }

        if (permissionState != PermissionState.Granted) {
            CommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textResId = if (permissionState == PermissionState.Denied) {
                    R.string.button_open_settings
                } else {
                    R.string.button_grant_permission
                },
                onClick = onButtonPressed
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAskForPermission() {
    PermissionHandlerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            PermissionContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                permissionName = "CAMERA",
                permissionIconDrawable = R.drawable.ic_camera,
                permissionState = PermissionState.AskForPermission,
                onButtonPressed = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewShowRationale() {
    PermissionHandlerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            PermissionContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                permissionName = "CAMERA",
                permissionIconDrawable = R.drawable.ic_camera,
                permissionState = PermissionState.ShowRationale,
                onButtonPressed = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDenied() {
    PermissionHandlerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            PermissionContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                permissionName = "CAMERA",
                permissionIconDrawable = R.drawable.ic_camera,
                permissionState = PermissionState.Denied,
                onButtonPressed = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewGranted() {
    PermissionHandlerTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            PermissionContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                permissionName = "CAMERA",
                permissionIconDrawable = R.drawable.ic_camera,
                permissionState = PermissionState.Granted,
                onButtonPressed = {},
            )
        }
    }
}
