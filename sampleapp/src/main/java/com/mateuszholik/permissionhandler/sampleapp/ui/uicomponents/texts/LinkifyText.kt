package com.mateuszholik.permissionhandler.sampleapp.ui.uicomponents.texts

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LinkifyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    linkColor: Color = MaterialTheme.colorScheme.tertiary,
    textAlign: TextAlign? = null,
) {
    val uriHandler = LocalUriHandler.current
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val urls = remember { extractUrls(text) }
    val annotatedString = remember {
        buildAnnotatedString {
            append(text)
            urls.forEach {
                addStyle(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = it.start,
                    end = it.end
                )
                addStringAnnotation(
                    tag = URL_STRING_ANNOTATION_TAG,
                    annotation = it.url,
                    start = it.start,
                    end = it.end,
                )
            }
        }
    }

    Text(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult?.let {
                    val position = it.getOffsetForPosition(offset)
                    annotatedString
                        .getStringAnnotations(position, position)
                        .firstOrNull()
                        ?.let { result ->
                            if (result.tag == URL_STRING_ANNOTATION_TAG) {
                                uriHandler.openUri(result.item)
                            }
                        }
                }
            }
        },
        onTextLayout = { layoutResult = it },
        text = annotatedString,
        color = color,
        textAlign = textAlign,
    )
}

private fun extractUrls(text: String): List<LinkInfo> =
    URL_REGEX.findAll(text).map {
        LinkInfo(
            url = it.value,
            start = it.range.first,
            end = it.range.last + 1
        )
    }.toList()

private data class LinkInfo(
    val url: String,
    val start: Int,
    val end: Int,
)

private const val URL_STRING_ANNOTATION_TAG = "URL"
private val URL_REGEX =
    """(http|ftp|https):\/\/([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:\/~+#-]*[\w@?^=%&\/~+#-])""".toRegex()

@Preview
@Composable
private fun Preview() {
    Surface {
        LinkifyText(
            modifier = Modifier.padding(16.dp),
            text = "Hello World!\nhttps://www.google.com\nHello World!\nhttps://www.google.com"
        )
    }
}
