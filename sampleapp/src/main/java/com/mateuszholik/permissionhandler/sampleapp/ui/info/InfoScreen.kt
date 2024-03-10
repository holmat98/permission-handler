package com.mateuszholik.permissionhandler.sampleapp.ui.info

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.mateuszholik.permissionhandler.sampleapp.R
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons.CommonOutlinedButton
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.scaffold.CommonScaffold
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.HeaderText
import com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts.TitleText

@Composable
fun InfoScreen(
    onBackPressed: () -> Unit,
    onPermissionHandlerLicensePressed: () -> Unit,
) {
    val context = LocalContext.current
    CommonScaffold(
        navigationIcon = {
            CommonIconButton(icon = Icons.Default.ArrowBack, onClick = onBackPressed)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(64.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                HeaderText(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    text = stringResource(R.string.information)
                )
                TitleText(text = stringResource(R.string.copyright))
            }

            CommonOutlinedButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.linkedin),
                icon = R.drawable.ic_linkedin,
                color = Color(0, 119, 181),
                onClick = { openWebsite(context, LINKED_IN_LINK) }
            )
            CommonOutlinedButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.github),
                icon = R.drawable.ic_github,
                color = Color(36, 41, 47),
                onClick = { openWebsite(context, GITHUB_REPOSITORY_LINK) }
            )
            CommonButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textResId = R.string.license,
                onClick = onPermissionHandlerLicensePressed,
            )
            CommonButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textResId = R.string.open_source_licenses,
                onClick = { openOssLicenses(context) },
            )
        }
    }
}

private fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        flags = FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

private fun openOssLicenses(context: Context) {
    val intent = Intent(context, OssLicensesMenuActivity::class.java).apply {
        flags = FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

private const val LINKED_IN_LINK = "https://linkedin.com/in/mateusz-holik"
private const val GITHUB_REPOSITORY_LINK = "https://github.com/holmat98/PermissionHandler"

@Preview
@Composable
private fun Preview() {
    PermissionHandlerTheme {
        InfoScreen(
            onBackPressed = {},
            onPermissionHandlerLicensePressed = {},
        )
    }
}
