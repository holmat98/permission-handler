package com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.rememberPermissionHandler
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons.CommonButton

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(title = { Text(text = "Handle Camera permission flow") })
        }
    ) {
        val paddingValues = PaddingValues(
            top = it.calculateTopPadding(),
            bottom = it.calculateBottomPadding(),
            start = 16.dp,
            end = 16.dp,
        )
        when (permissionHandler.currentPermissionState) {
            is PermissionState.ShowRationale,
            is PermissionState.AskForPermission -> {
                Content(
                    text = stringResource(R.string.permission_show_message),
                    buttonResId = R.string.button_grant_permission,
                    paddingValues = paddingValues,
                    onClick = permissionHandler.launchPermissionDialog
                )
            }
            is PermissionState.Denied -> {
                Content(
                    text = stringResource(R.string.permission_declined),
                    buttonResId = R.string.button_open_settings,
                    paddingValues = paddingValues,
                    onClick = permissionHandler.launchPermissionDialog
                )
            }
            PermissionState.Granted -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        text = stringResource(R.string.permission_granted),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    text: String,
    @StringRes buttonResId: Int,
    paddingValues: PaddingValues,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = text,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        CommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textResId = buttonResId,
            onClick = onClick
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Content(
            text = stringResource(R.string.permission_show_message),
            buttonResId = R.string.button_grant_permission,
            paddingValues = PaddingValues(16.dp),
            onClick = {}
        )
    }
}
