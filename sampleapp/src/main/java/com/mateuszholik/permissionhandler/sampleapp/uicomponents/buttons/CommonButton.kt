package com.mateuszholik.permissionhandler.sampleapp.uicomponents.buttons

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonButton(
    @StringRes textResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Button(
        modifier = modifier.height(75.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Text(
            fontSize = 16.sp,
            text = stringResource(textResId)
        )
    }
}
