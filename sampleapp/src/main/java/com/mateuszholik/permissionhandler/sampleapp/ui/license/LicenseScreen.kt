package com.mateuszholik.permissionhandler.sampleapp.ui.license

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.scaffold.CommonScaffold
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts.HeaderText

@Composable
fun LicenseScreen(onBackPressed: () -> Unit) {
    CommonScaffold(
        navigationIcon = {
            CommonIconButton(icon = Icons.Default.ArrowBack, onClick = onBackPressed)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalAlignment =Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Icon(
                    modifier = Modifier.size(64.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                HeaderText(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    text = stringResource(R.string.license),
                    textAlign = TextAlign.Center,
                )
            }
            item {
                Text(
                    text = stringResource(R.string.permission_handler_license),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PermissionHandlerTheme {
        LicenseScreen(onBackPressed = {})
    }
}
