package com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    textAlign: TextAlign? = null,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = textAlign,
        lineHeight = 34.sp,
    )
}

@Preview
@Composable
private fun Preview() {
    Surface {
        HeaderText(
            modifier = Modifier.padding(16.dp),
            text = "Header"
        )
    }
}
