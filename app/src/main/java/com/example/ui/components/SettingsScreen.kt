package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppSettings
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
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showVoiceMenu by remember { mutableStateOf(false) }
    val voiceOptions = listOf(
        "Aether Nova (Calm / Precise)",
        "Aether Orion (Authoritative / Warm)",
        "Aether Lyra (Analytical / Fast)",
        "Aether Sol (Subtle / Resonant)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_settings_back")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "SETTINGS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrayMuted
                )
                Text(
                    text = "Preferences & Diagnostics",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
            }
        }

        Divider(color = BorderDark, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status & Model Badge Card with Official Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ahadu_official_logo),
                        contentDescription = "AHADU Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "ALPHA NEURAL ARCHITECTURE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrayMuted
                        )
                        Text(
                            text = "ALPHA-v1.4 Enterprise Mesh",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Text(
                            text = "Inference Engine: Aether Neural Core",
                            style = MaterialTheme.typography.bodySmall,
                            color = GraySubtle
                        )
                    }
                }
            }

            // Section 1: Appearance & Interface
            SettingsSectionHeader(title = "APPEARANCE & INTERFACE", icon = Icons.Default.Palette)

            SettingsCard {
                SettingsSwitchRow(
                    title = "Streaming Output",
                    subtitle = "Stream response tokens with natural cadence",
                    checked = settings.streamResponses,
                    onCheckedChange = { onUpdateSettings(settings.copy(streamResponses = it)) }
                )

                Divider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))

                SettingsSwitchRow(
                    title = "Hardware Acceleration",
                    subtitle = "Utilize hardware rendering acceleration",
                    checked = settings.hardwareAcceleration,
                    onCheckedChange = { onUpdateSettings(settings.copy(hardwareAcceleration = it)) }
                )

                Divider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))

                SettingsSwitchRow(
                    title = "Haptic Tactile Feedback",
                    subtitle = "Subtle vibration feedback upon stream completions",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedback = it)) }
                )
            }

            // Section 2: Voice & Audio
            SettingsSectionHeader(title = "VOICE & SENSORIUM", icon = Icons.Default.Mic)

            SettingsCard {
                Column {
                    Text(
                        text = "Active ALPHA Neural Voice",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "Synthesized vocal timbre for speech conversations",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedButton(
                            onClick = { showVoiceMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = settings.activeVoiceName, color = PureWhite)
                        }

                        DropdownMenu(
                            expanded = showVoiceMenu,
                            onDismissRequest = { showVoiceMenu = false },
                            modifier = Modifier.background(ElevatedDark)
                        ) {
                            voiceOptions.forEach { voice ->
                                DropdownMenuItem(
                                    text = { Text(voice, color = PureWhite) },
                                    onClick = {
                                        onUpdateSettings(settings.copy(activeVoiceName = voice))
                                        showVoiceMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Privacy & Data Controls
            SettingsSectionHeader(title = "DATA GOVERNANCE & PRIVACY", icon = Icons.Default.Security)

            SettingsCard {
                SettingsSwitchRow(
                    title = "Anonymous Telemetry",
                    subtitle = "Transmit execution latency metrics to Aether mesh",
                    checked = settings.telemetryEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(telemetryEnabled = it)) }
                )

                Divider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Purge Local Cache",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "Deletes cached memory and temporary attachments",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayMuted
                        )
                    }
                    TextButton(
                        onClick = {
                            Toast.makeText(context, "Local cache purged", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Purge", color = PureWhite)
                    }
                }
            }

            // Section 4: About Ahadu
            SettingsSectionHeader(title = "ABOUT AHADU", icon = Icons.Default.Info)

            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "AHADU AI Platform",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "Developed by Aether Industries. Engineered with sovereign ALPHA neural architecture. Built for high-reliability, zero-latency conversational intelligence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version: 1.4.0 (Aether System Build)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = GraySubtle
                    )
                    Text(
                        text = "Neural Core: ALPHA-1024",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrayMuted
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp,
                color = PureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GrayMuted
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureBlack,
                checkedTrackColor = PureWhite,
                uncheckedThumbColor = GrayMuted,
                uncheckedTrackColor = ElevatedDark
            )
        )
    }
}
