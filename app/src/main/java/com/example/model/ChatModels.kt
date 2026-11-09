package com.example.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

enum class AlphaState {
    IDLE,
    THINKING,
    SEARCHING,
    PROCESSING,
    RESPONDING,
    ERROR
}

data class AttachmentItem(
    val id: String,
    val name: String,
    val type: AttachmentType,
    val sizeText: String,
    val previewUri: String? = null
)

enum class AttachmentType {
    IMAGE,
    DOCUMENT,
    CODE,
    AUDIO
}

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val alphaState: AlphaState = AlphaState.IDLE,
    val isStreaming: Boolean = false,
    val attachments: List<AttachmentItem> = emptyList(),
    val webSearchUsed: Boolean = false,
    val sources: List<String> = emptyList(),
    val latencyMs: Long? = null,
    val modelName: String = "ALPHA-v1.4 Neural"
)

data class Conversation(
    val id: String,
    val title: String,
    val preview: String,
    val updatedAt: Long,
    val messagesCount: Int = 0,
    val isPinned: Boolean = false
)

enum class WebSearchMode {
    OFF,
    AUTO,
    ALWAYS
}

data class AppSettings(
    val streamResponses: Boolean = true,
    val hapticFeedback: Boolean = true,
    val webSearchMode: WebSearchMode = WebSearchMode.AUTO,
    val activeVoiceName: String = "Aether Nova (Calm / Precise)",
    val language: String = "English (Universal)",
    val telemetryEnabled: Boolean = false,
    val hardwareAcceleration: Boolean = true,
    val alphaInferenceEndpoint: String = "https://alpha-mesh.aetherindustries.internal/v1",
    val developerMode: Boolean = true
)
