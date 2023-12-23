package com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.sampleapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePermissionScreen(viewModel: SinglePermissionViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { result -> viewModel.handlePermissionResult(result) }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { viewModel.handleBackFromSettings() }

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
        when (state) {
            is SinglePermissionState.ShowRationale -> {
                val permission = (state as SinglePermissionState.ShowRationale).permission
                Content(
                    text = stringResource(R.string.permission_show_rationale, permission),
                    buttonResId = R.string.button_grant_permission,
                    paddingValues = paddingValues,
                    onClick = { permissionLauncher.launch(permission) }
                )
            }
            is SinglePermissionState.AskForPermission -> {
                val permission = (state as SinglePermissionState.AskForPermission).permission
                Content(
                    text = stringResource(R.string.permission_never_asked, permission),
                    buttonResId = R.string.button_grant_permission,
                    paddingValues = paddingValues,
                    onClick = { permissionLauncher.launch(permission) }
                )
            }
            is SinglePermissionState.Denied -> {
                Content(
                    text = stringResource(R.string.permission_declined),
                    buttonResId = R.string.button_open_settings,
                    paddingValues = paddingValues,
                    onClick = {
                        settingsLauncher.launch(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }
                )
            }
            is SinglePermissionState.Granted -> {
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

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = onClick
        ) {
            Text(text = stringResource(buttonResId))
        }
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
            text = stringResource(R.string.permission_never_asked, "Camera"),
            buttonResId = R.string.button_grant_permission,
            paddingValues = PaddingValues(16.dp),
            onClick = {}
        )
    }
}
