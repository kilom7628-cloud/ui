package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.GraySubtle
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    message: ChatMessage,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var feedbackState by remember { mutableStateOf<Boolean?>(null) } // true=up, false=down

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Author Tag & Timestamp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            if (!isUser) {
                // ALPHA Identity Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElevatedDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ALPHA // AETHER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Text(
                    text = "OPERATOR",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrayMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            val timeStr = remember(message.timestamp) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
            }
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall,
                color = GraySubtle
            )

            if (!isUser && message.latencyMs != null) {
                Text(
                    text = " • ${message.latencyMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = GraySubtle
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Message Content Box
        Box(
            modifier = Modifier
                .widthIn(max = 580.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .background(if (isUser) ElevatedDark else SurfaceDark)
                .border(
                    width = 1.dp,
                    color = if (isUser) BorderLight else BorderDark,
                    shape = RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                // If web search was used, display search citation tag
                if (message.webSearchUsed) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(BackgroundDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Web Search telemetry integrated",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayMuted
                        )
                    }
                }

                // Render Markdown/Code blocks
                RenderRichMessageContent(content = message.content)
            }
        }

        // Assistant Message Action Footer (Copy, Regenerate, Feedback)
        if (!isUser) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(message.content))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("action_copy_${message.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = GraySubtle,
                        modifier = Modifier.size(14.dp)
                    )
                }

                IconButton(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("action_regenerate_${message.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate response",
                        tint = GraySubtle,
                        modifier = Modifier.size(14.dp)
                    )
                }

                IconButton(
                    onClick = {
                        feedbackState = if (feedbackState == true) null else true
                        Toast.makeText(context, "Feedback recorded", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Helpful",
                        tint = if (feedbackState == true) PureWhite else GraySubtle,
                        modifier = Modifier.size(14.dp)
                    )
                }

                IconButton(
                    onClick = {
                        feedbackState = if (feedbackState == false) null else false
                        Toast.makeText(context, "Feedback recorded", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Not helpful",
                        tint = if (feedbackState == false) PureWhite else GraySubtle,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Splits message text into regular markdown-formatted prose and fenced code blocks
 */
@Composable
private fun RenderRichMessageContent(content: String) {
    val blocks = remember(content) { parseMarkdownBlocks(content) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is ContentBlock.Text -> {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OffWhite
                    )
                }
                is ContentBlock.Code -> {
                    CodeBlockView(
                        code = block.code,
                        language = block.language.ifEmpty { "kotlin" }
                    )
                }
            }
        }
    }
}

private sealed class ContentBlock {
    data class Text(val text: String) : ContentBlock()
    data class Code(val language: String, val code: String) : ContentBlock()
}

private fun parseMarkdownBlocks(raw: String): List<ContentBlock> {
    val list = mutableListOf<ContentBlock>()
    val lines = raw.lines()
    var inCode = false
    var codeLang = ""
    val currentCode = StringBuilder()
    val currentText = StringBuilder()

    for (line in lines) {
        if (line.trim().startsWith("```")) {
            if (inCode) {
                list.add(ContentBlock.Code(language = codeLang, code = currentCode.toString()))
                currentCode.clear()
                inCode = false
            } else {
                if (currentText.isNotEmpty()) {
                    list.add(ContentBlock.Text(currentText.toString().trim()))
                    currentText.clear()
                }
                codeLang = line.trim().removePrefix("```").trim()
                inCode = true
            }
        } else {
            if (inCode) {
                currentCode.append(line).append("\n")
            } else {
                currentText.append(line).append("\n")
            }
        }
    }

    if (inCode && currentCode.isNotEmpty()) {
        list.add(ContentBlock.Code(language = codeLang, code = currentCode.toString()))
    } else if (currentText.isNotEmpty()) {
        list.add(ContentBlock.Text(currentText.toString().trim()))
    }

    return if (list.isEmpty()) listOf(ContentBlock.Text(raw)) else list
}
