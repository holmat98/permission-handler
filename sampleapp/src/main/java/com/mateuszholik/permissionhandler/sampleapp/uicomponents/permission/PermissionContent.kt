package com.mateuszholik.permissionhandler.sampleapp.uicomponents.permission

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.CommonText
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.HeaderText
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.TitleText

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
            HeaderText(
                text = permissionName,
                textAlign = TextAlign.Center,
            )
            TitleText(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.current_state),
            )
            CommonText(
                text = permissionState::class.java.simpleName,
                fontWeight = FontWeight.Bold,
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
