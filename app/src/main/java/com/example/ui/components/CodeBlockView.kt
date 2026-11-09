package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CodeBoxBackground
import com.example.ui.theme.CodeTextKeyword
import com.example.ui.theme.CodeTextMuted
import com.example.ui.theme.CodeTextPrimary
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.PureWhite

@Composable
fun CodeBlockView(
    code: String,
    language: String = "kotlin",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val highlightedCode = buildMonochromeHighlightedCode(code.trim())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBoxBackground)
            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
    ) {
        Column {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElevatedDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = language.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code.trim()))
                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = GrayMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Code content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .horizontalScroll(scrollState)
            ) {
                Text(
                    text = highlightedCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = CodeTextPrimary
                )
            }
        }
    }
}

/**
 * Monochrome syntax highlighter using font weights and grayscale values
 */
private fun buildMonochromeHighlightedCode(code: String): AnnotatedString {
    val keywords = setOf(
        "class", "fun", "val", "var", "interface", "object", "return",
        "if", "else", "when", "for", "while", "import", "package",
        "private", "public", "protected", "override", "data", "sealed",
        "def", "async", "await", "const", "let", "function", "export"
    )

    return buildAnnotatedString {
        val lines = code.lines()
        lines.forEachIndexed { lineIdx, line ->
            var i = 0
            while (i < line.length) {
                // Comments
                if (line.startsWith("//", i) || line.startsWith("#", i)) {
                    val commentText = line.substring(i)
                    pushStyle(SpanStyle(color = CodeTextMuted))
                    append(commentText)
                    pop()
                    break
                }

                // String literals
                if (line[i] == '"' || line[i] == '\'') {
                    val quote = line[i]
                    val nextQuote = line.indexOf(quote, i + 1)
                    val strText = if (nextQuote != -1) {
                        line.substring(i, nextQuote + 1)
                    } else {
                        line.substring(i)
                    }
                    pushStyle(SpanStyle(color = CodeTextPrimary, fontWeight = FontWeight.Normal))
                    append(strText)
                    pop()
                    i += strText.length
                    continue
                }

                // Words / tokens
                if (line[i].isLetter()) {
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) {
                        i++
                    }
                    val token = line.substring(start, i)
                    when {
                        token in keywords -> {
                            pushStyle(SpanStyle(color = CodeTextKeyword, fontWeight = FontWeight.Bold))
                            append(token)
                            pop()
                        }
                        else -> {
                            pushStyle(SpanStyle(color = CodeTextPrimary))
                            append(token)
                            pop()
                        }
                    }
                    continue
                }

                append(line[i])
                i++
            }
            if (lineIdx < lines.lastIndex) {
                append("\n")
            }
        }
    }
}
