package com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateuszholik.permissionhandler.sampleapp.R

@Composable
fun CommonOutlinedButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = color,
        ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )
        Text(text = text)
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        CommonOutlinedButton(
            modifier = Modifier.padding(16.dp),
            text = "Click me!",
            icon = R.drawable.ic_notification,
            onClick = {},
        )
    }
}
