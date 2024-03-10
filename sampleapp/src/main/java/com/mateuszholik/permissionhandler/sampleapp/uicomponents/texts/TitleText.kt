package com.mateuszholik.permissionhandler.sampleapp.uicomponents.texts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = LocalContentColor.current,
) {
    Text(
        modifier = modifier,
        fontSize = 28.sp,
        text = text,
        color = color,
        fontWeight = fontWeight,
    )
}

@Preview
@Composable
private fun Preview() {
    Surface {
        TitleText(
            modifier = Modifier.padding(16.dp),
            text = "Title"
        )
    }
}
