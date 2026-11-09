package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlphaState
import com.example.model.AttachmentItem
import com.example.model.AttachmentType
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.GraySubtle
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark

@Composable
fun ChatComposer(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    alphaState: AlphaState,
    attachments: List<AttachmentItem>,
    onRemoveAttachment: (String) -> Unit,
    onAddAttachmentClick: () -> Unit,
    webSearchActive: Boolean,
    onToggleWebSearch: () -> Unit,
    isRecordingVoice: Boolean,
    onToggleVoiceRecording: () -> Unit,
    onOpenCodeWorkspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBusy = alphaState != AlphaState.IDLE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                if (isRecordingVoice) PureWhite else BorderDark,
                RoundedCornerShape(18.dp)
            )
            .padding(10.dp)
    ) {
        // Attachment badges preview row
        if (attachments.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(attachments, key = { it.id }) { item ->
                    AttachmentBadge(
                        item = item,
                        onRemove = { onRemoveAttachment(item.id) }
                    )
                }
            }
        }

        // Voice active warning banner
        AnimatedVisibility(visible = isRecordingVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElevatedDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AlphaEnergyVisual(
                    state = AlphaState.PROCESSING,
                    size = 14.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AETHER VOICE INPUT ACTIVE // LISTENING...",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = PureWhite
                )
            }
        }

        // Main text input
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "Message AHADU...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GraySubtle
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 46.dp, max = 130.dp)
                .testTag("chat_input_field"),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tool controls & Action bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment button
            IconButton(
                onClick = onAddAttachmentClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_attach_media")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach file",
                    tint = if (attachments.isNotEmpty()) PureWhite else GrayMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Web Search toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (webSearchActive) ElevatedDark else Color.Transparent)
                    .border(1.dp, if (webSearchActive) BorderLight else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { onToggleWebSearch() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("btn_toggle_web_search")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Web Search",
                        tint = if (webSearchActive) PureWhite else GrayMuted,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Web",
                        fontSize = 11.sp,
                        fontWeight = if (webSearchActive) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                        color = if (webSearchActive) PureWhite else GrayMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Code Workspace launcher button
            IconButton(
                onClick = onOpenCodeWorkspace,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_code_workspace")
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "Code Workspace",
                    tint = GrayMuted,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Voice Microphone
            IconButton(
                onClick = onToggleVoiceRecording,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_voice_input")
            ) {
                Icon(
                    imageVector = if (isRecordingVoice) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice mode",
                    tint = if (isRecordingVoice) PureWhite else GrayMuted,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Primary Send or Stop button
            if (isBusy) {
                IconButton(
                    onClick = onStop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .testTag("btn_stop_generation")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop generation",
                        tint = PureBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                val canSend = text.isNotBlank() || attachments.isNotEmpty()
                IconButton(
                    onClick = onSend,
                    enabled = canSend,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (canSend) PureWhite else BorderDark)
                        .testTag("btn_send_message")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send message",
                        tint = if (canSend) PureBlack else GraySubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentBadge(
    item: AttachmentItem,
    onRemove: () -> Unit
) {
    val icon = when (item.type) {
        AttachmentType.IMAGE -> Icons.Default.Image
        AttachmentType.DOCUMENT -> Icons.Default.Description
        AttachmentType.CODE -> Icons.Default.Code
        AttachmentType.AUDIO -> Icons.Default.Mic
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ElevatedDark)
            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = OffWhite,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = GrayMuted,
            modifier = Modifier
                .size(14.dp)
                .clickable { onRemove() }
        )
    }
}
