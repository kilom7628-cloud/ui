package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AttachmentType
import com.example.ui.components.AlphaStatusIndicator
import com.example.ui.components.ChatComposer
import com.example.ui.components.CodeWorkspaceModal
import com.example.ui.components.FloatingSidebar
import com.example.ui.components.MessageBubble
import com.example.ui.components.SettingsScreen
import com.example.ui.components.WelcomeScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.AppView
import com.example.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.messages.size, uiState.messages.lastOrNull()?.content?.length) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    BackHandler(enabled = uiState.isSidebarOpen || uiState.currentView != AppView.CHAT) {
        if (uiState.isSidebarOpen) {
            viewModel.toggleSidebar(false)
        } else if (uiState.currentView != AppView.CHAT) {
            viewModel.setView(AppView.CHAT)
        }
    }

    when (uiState.currentView) {
        AppView.SETTINGS -> {
            SettingsScreen(
                settings = uiState.settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onBack = { viewModel.setView(AppView.CHAT) }
            )
            return
        }
        AppView.CODE_WORKSPACE -> {
            CodeWorkspaceModal(
                onDismiss = { viewModel.setView(AppView.CHAT) }
            )
            return
        }
        AppView.CHAT -> {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleSidebar(true) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_open_sidebar")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open sidebar",
                            tint = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Center Brand / Model Status Pill with official logo
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                            .clickable { showTopMenu = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ahadu_official_logo),
                            contentDescription = "AHADU Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AHADU // ALPHA",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Code Workspace Shortcut
                    IconButton(
                        onClick = { viewModel.setView(AppView.CODE_WORKSPACE) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_header_code")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Code workspace",
                            tint = GrayMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // New Chat
                    IconButton(
                        onClick = { viewModel.startNewConversation() },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_header_new_chat")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New chat",
                            tint = PureWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showTopMenu = true },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = GrayMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showTopMenu,
                            onDismissRequest = { showTopMenu = false },
                            modifier = Modifier.background(ElevatedDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings", color = PureWhite) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = PureWhite
                                    )
                                },
                                onClick = {
                                    showTopMenu = false
                                    viewModel.setView(AppView.SETTINGS)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Code Workspace", color = PureWhite) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = PureWhite
                                    )
                                },
                                onClick = {
                                    showTopMenu = false
                                    viewModel.setView(AppView.CODE_WORKSPACE)
                                }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    AlphaStatusIndicator(
                        state = uiState.alphaState,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 6.dp)
                    )

                    ChatComposer(
                        text = uiState.inputText,
                        onTextChange = { viewModel.onInputTextChange(it) },
                        onSend = { viewModel.sendMessage() },
                        onStop = { viewModel.stopGeneration() },
                        alphaState = uiState.alphaState,
                        attachments = uiState.currentAttachments,
                        onRemoveAttachment = { viewModel.removeAttachment(it) },
                        onAddAttachmentClick = { showAttachmentSheet = true },
                        webSearchActive = uiState.isWebSearchActive,
                        onToggleWebSearch = { viewModel.toggleWebSearch() },
                        isRecordingVoice = uiState.isVoiceRecording,
                        onToggleVoiceRecording = { viewModel.toggleVoiceRecording() },
                        onOpenCodeWorkspace = { viewModel.setView(AppView.CODE_WORKSPACE) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.messages.isEmpty()) {
                    WelcomeScreen(
                        onSelectPrompt = { prompt ->
                            viewModel.sendMessage(prompt)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(
                                message = message,
                                onRegenerate = { viewModel.regenerateLast() }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // Floating Sidebar Overlay
        FloatingSidebar(
            isOpen = uiState.isSidebarOpen,
            onClose = { viewModel.toggleSidebar(false) },
            conversations = uiState.conversations,
            activeConversationId = uiState.activeConversationId,
            onSelectConversation = { viewModel.selectConversation(it) },
            onNewChat = { viewModel.startNewConversation() },
            onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
            onDeleteConversation = { viewModel.deleteConversation(it) },
            onOpenSettings = { viewModel.setView(AppView.SETTINGS) }
        )

        // Attachment Sheet
        if (showAttachmentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAttachmentSheet = false },
                containerColor = ElevatedDark,
                scrimColor = PureBlack.copy(alpha = 0.75f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "ADD ATTACHMENT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    AttachmentOptionRow(
                        icon = Icons.Default.Image,
                        title = "Visual Image / Photo",
                        subtitle = "Attach diagram, interface screenshot, or photo",
                        onClick = {
                            viewModel.addMockAttachment(AttachmentType.IMAGE)
                            showAttachmentSheet = false
                            Toast.makeText(context, "Image attached", Toast.LENGTH_SHORT).show()
                        }
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.Description,
                        title = "Document / Text File",
                        subtitle = "Attach PDF, technical paper, or notes",
                        onClick = {
                            viewModel.addMockAttachment(AttachmentType.DOCUMENT)
                            showAttachmentSheet = false
                            Toast.makeText(context, "Document attached", Toast.LENGTH_SHORT).show()
                        }
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.Code,
                        title = "Source Code File",
                        subtitle = "Attach Kotlin, Python, or C++ file",
                        onClick = {
                            viewModel.addMockAttachment(AttachmentType.CODE)
                            showAttachmentSheet = false
                            Toast.makeText(context, "Code attached", Toast.LENGTH_SHORT).show()
                        }
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.Mic,
                        title = "Audio Recording",
                        subtitle = "Attach recorded voice clip",
                        onClick = {
                            viewModel.addMockAttachment(AttachmentType.AUDIO)
                            showAttachmentSheet = false
                            Toast.makeText(context, "Audio attached", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AttachmentOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ElevatedDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
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
    }
}
