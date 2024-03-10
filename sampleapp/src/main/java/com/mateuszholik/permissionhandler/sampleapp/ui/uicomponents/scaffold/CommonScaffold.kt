package com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons.CommonIconButton
import com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts.TitleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = navigationIcon,
                actions = actions,
            )
        },
        content = {
            content(
                PaddingValues(
                    start = 16.dp,
                    top = it.calculateTopPadding(),
                    end = 16.dp,
                    bottom = it.calculateBottomPadding(),
                )
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    CommonScaffold(
        navigationIcon = {
            CommonIconButton(icon = Icons.Default.ArrowBack, onClick = {})
        },
        actions = {
            CommonIconButton(icon = Icons.Default.Add, onClick = {})
            CommonIconButton(icon = Icons.Default.Info, onClick = {})
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            repeat(5) { number ->
                TitleText(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = "Title $number"
                )
            }
        }
    }
}
