package com.mateuszholik.permissionhandler.sampleapp.ui.license

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts.CommonText
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts.HeaderText
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts.LinkifyText

@Composable
fun LicenseScreen(onBackPressed: () -> Unit) {
    val listState = rememberLazyListState()
    val shouldShowTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1
        }
    }

    CommonScaffold(
        navigationIcon = {
            CommonIconButton(icon = Icons.Default.ArrowBack, onClick = onBackPressed)
        },
        title = {
            if (shouldShowTitle) {
                CommonText(text = stringResource(R.string.license))
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            state = listState,
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
                LinkifyText(
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
