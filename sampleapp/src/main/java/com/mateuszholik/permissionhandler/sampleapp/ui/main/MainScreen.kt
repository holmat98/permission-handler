package com.mateuszholik.permissionhandler.sampleapp.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mateuszholik.permissionhandler.sampleapp.BuildConfig
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons.CommonButton

@Composable
fun MainScreen(
    onCameraPermissionPressed: () -> Unit,
    onLocationPermissionPressed: () -> Unit,
) {
    Scaffold {
        Column(
            modifier = Modifier.padding(
                PaddingValues(
                    top = it.calculateTopPadding(),
                    start = 16.dp,
                    bottom = it.calculateBottomPadding(),
                    end = 16.dp,
                )
            )
        ) {
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
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = BuildConfig.VERSION_NAME,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
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
            )
        }
    }
}
