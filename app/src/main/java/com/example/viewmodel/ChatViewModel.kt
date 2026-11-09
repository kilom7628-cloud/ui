package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ConversationRepository
import com.example.model.AlphaState
import com.example.model.AppSettings
import com.example.model.AttachmentItem
import com.example.model.AttachmentType
import com.example.model.ChatMessage
import com.example.model.Conversation
import com.example.model.MessageRole
import com.example.model.WebSearchMode
import com.example.service.AlphaInferenceService
import com.example.service.AlphaInferenceServiceImpl
import com.example.service.AlphaStreamEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppView {
    CHAT,
    SETTINGS,
    CODE_WORKSPACE
}

data class ChatUiState(
    val currentView: AppView = AppView.CHAT,
    val conversations: List<Conversation> = emptyList(),
    val activeConversationId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val alphaState: AlphaState = AlphaState.IDLE,
    val currentAttachments: List<AttachmentItem> = emptyList(),
    val isWebSearchActive: Boolean = false,
    val isVoiceRecording: Boolean = false,
    val isSidebarOpen: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val statusText: String? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConversationRepository(application)
    private val inferenceService: AlphaInferenceService = AlphaInferenceServiceImpl()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var activeStreamJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val conversations = repository.loadConversations()
        val firstId = conversations.firstOrNull()?.id
        val initialMessages = if (firstId != null) repository.loadMessages(firstId) else emptyList()

        _uiState.update {
            it.copy(
                conversations = conversations,
                activeConversationId = firstId,
                messages = initialMessages
            )
        }
    }

    fun setView(view: AppView) {
        _uiState.update { it.copy(currentView = view) }
    }

    fun toggleSidebar(open: Boolean? = null) {
        _uiState.update { it.copy(isSidebarOpen = open ?: !it.isSidebarOpen) }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun toggleWebSearch() {
        _uiState.update { it.copy(isWebSearchActive = !it.isWebSearchActive) }
    }

    fun toggleVoiceRecording() {
        val recording = !_uiState.value.isVoiceRecording
        _uiState.update { it.copy(isVoiceRecording = recording) }
        if (!recording) {
            // Emulate captured voice speech-to-text
            val current = _uiState.value.inputText
            val voiceTranscript = if (current.isBlank()) "Explain the ALPHA cognitive tensor routing architecture." else "$current (voice transcript synthesized)"
            _uiState.update { it.copy(inputText = voiceTranscript) }
        }
    }

    fun addMockAttachment(type: AttachmentType) {
        val item = when (type) {
            AttachmentType.IMAGE -> AttachmentItem(
                id = UUID.randomUUID().toString(),
                name = "quantum_neural_topology.png",
                type = AttachmentType.IMAGE,
                sizeText = "1.4 MB"
            )
            AttachmentType.DOCUMENT -> AttachmentItem(
                id = UUID.randomUUID().toString(),
                name = "Aether_ALPHA_Specification_v1.4.pdf",
                type = AttachmentType.DOCUMENT,
                sizeText = "340 KB"
            )
            AttachmentType.CODE -> AttachmentItem(
                id = UUID.randomUUID().toString(),
                name = "tensor_ops.kt",
                type = AttachmentType.CODE,
                sizeText = "12 KB"
            )
            AttachmentType.AUDIO -> AttachmentItem(
                id = UUID.randomUUID().toString(),
                name = "operator_voice_stream.wav",
                type = AttachmentType.AUDIO,
                sizeText = "850 KB"
            )
        }
        _uiState.update { it.copy(currentAttachments = it.currentAttachments + item) }
    }

    fun removeAttachment(id: String) {
        _uiState.update {
            it.copy(currentAttachments = it.currentAttachments.filterNot { item -> item.id == id })
        }
    }

    fun selectConversation(id: String) {
        activeStreamJob?.cancel()
        val messages = repository.loadMessages(id)
        _uiState.update {
            it.copy(
                activeConversationId = id,
                messages = messages,
                alphaState = AlphaState.IDLE,
                inputText = "",
                currentAttachments = emptyList()
            )
        }
    }

    fun startNewConversation() {
        activeStreamJob?.cancel()
        val newId = "convo_${System.currentTimeMillis()}"
        val newConversation = Conversation(
            id = newId,
            title = "New Synapse Session",
            preview = "",
            updatedAt = System.currentTimeMillis(),
            messagesCount = 0
        )
        val updatedList = listOf(newConversation) + _uiState.value.conversations
        repository.saveConversations(updatedList)

        _uiState.update {
            it.copy(
                conversations = updatedList,
                activeConversationId = newId,
                messages = emptyList(),
                alphaState = AlphaState.IDLE,
                inputText = "",
                currentAttachments = emptyList()
            )
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        val updated = _uiState.value.conversations.map {
            if (it.id == id) it.copy(title = newTitle) else it
        }
        repository.saveConversations(updated)
        _uiState.update { it.copy(conversations = updated) }
    }

    fun deleteConversation(id: String) {
        repository.deleteConversation(id)
        val updated = _uiState.value.conversations.filterNot { it.id == id }
        repository.saveConversations(updated)

        val nextActive = updated.firstOrNull()?.id
        val nextMessages = if (nextActive != null) repository.loadMessages(nextActive) else emptyList()

        _uiState.update {
            it.copy(
                conversations = updated,
                activeConversationId = nextActive,
                messages = nextMessages
            )
        }
    }

    fun updateSettings(settings: AppSettings) {
        _uiState.update { it.copy(settings = settings) }
    }

    fun sendMessage(customPrompt: String? = null) {
        val prompt = (customPrompt ?: _uiState.value.inputText).trim()
        val attachments = _uiState.value.currentAttachments
        if (prompt.isEmpty() && attachments.isEmpty()) return

        var activeId = _uiState.value.activeConversationId
        var convos = _uiState.value.conversations

        // If no conversation exists or starting fresh
        if (activeId == null || convos.none { it.id == activeId }) {
            activeId = "convo_${System.currentTimeMillis()}"
            val newConvo = Conversation(
                id = activeId,
                title = prompt.take(32).ifEmpty { "Synapse Session" },
                preview = prompt.take(60),
                updatedAt = System.currentTimeMillis(),
                messagesCount = 1
            )
            convos = listOf(newConvo) + convos
            repository.saveConversations(convos)
        }

        val userMessage = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            role = MessageRole.USER,
            content = prompt,
            timestamp = System.currentTimeMillis(),
            attachments = attachments
        )

        val updatedMessages = _uiState.value.messages + userMessage
        repository.saveMessages(activeId, updatedMessages)

        // Update conversation preview and title if first user prompt
        val updatedConvos = convos.map {
            if (it.id == activeId) {
                it.copy(
                    title = if (it.title == "New Synapse Session") prompt.take(36) else it.title,
                    preview = prompt.take(60),
                    updatedAt = System.currentTimeMillis(),
                    messagesCount = updatedMessages.size
                )
            } else it
        }
        repository.saveConversations(updatedConvos)

        _uiState.update {
            it.copy(
                activeConversationId = activeId,
                conversations = updatedConvos,
                messages = updatedMessages,
                inputText = "",
                currentAttachments = emptyList(),
                alphaState = AlphaState.THINKING
            )
        }

        // Trigger ALPHA Inference Stream
        streamAlphaResponse(
            convoId = activeId,
            prompt = prompt,
            history = updatedMessages,
            attachments = attachments,
            webSearch = _uiState.value.isWebSearchActive
        )
    }

    fun regenerateLast() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == MessageRole.USER } ?: return
        val activeId = _uiState.value.activeConversationId ?: return

        // Remove last assistant message if present
        val pruned = _uiState.value.messages.toMutableList()
        if (pruned.isNotEmpty() && pruned.last().role == MessageRole.ASSISTANT) {
            pruned.removeAt(pruned.lastIndex)
        }

        _uiState.update { it.copy(messages = pruned, alphaState = AlphaState.THINKING) }
        repository.saveMessages(activeId, pruned)

        streamAlphaResponse(
            convoId = activeId,
            prompt = lastUserMessage.content,
            history = pruned,
            attachments = lastUserMessage.attachments,
            webSearch = _uiState.value.isWebSearchActive
        )
    }

    fun stopGeneration() {
        activeStreamJob?.cancel()
        _uiState.update { it.copy(alphaState = AlphaState.IDLE) }
    }

    private fun streamAlphaResponse(
        convoId: String,
        prompt: String,
        history: List<ChatMessage>,
        attachments: List<AttachmentItem>,
        webSearch: Boolean
    ) {
        activeStreamJob?.cancel()

        activeStreamJob = viewModelScope.launch {
            val placeholderMsgId = "alpha_${System.currentTimeMillis()}"
            var streamingMsg = ChatMessage(
                id = placeholderMsgId,
                role = MessageRole.ASSISTANT,
                content = "",
                alphaState = AlphaState.THINKING,
                isStreaming = true,
                modelName = "ALPHA-v1.4 Neural"
            )

            // Add placeholder assistant message
            _uiState.update {
                it.copy(
                    messages = it.messages + streamingMsg,
                    alphaState = AlphaState.THINKING
                )
            }

            try {
                inferenceService.streamResponse(
                    prompt = prompt,
                    history = history,
                    attachments = attachments,
                    enableWebSearch = webSearch
                ).collect { event ->
                    when (event) {
                        is AlphaStreamEvent.StateChange -> {
                            _uiState.update { it.copy(alphaState = event.state) }
                        }
                        is AlphaStreamEvent.ContentChunk -> {
                            streamingMsg = streamingMsg.copy(
                                content = event.accumulatedText,
                                isStreaming = true
                            )
                            _uiState.update { state ->
                                val list = state.messages.map { m ->
                                    if (m.id == placeholderMsgId) streamingMsg else m
                                }
                                state.copy(messages = list)
                            }
                        }
                        is AlphaStreamEvent.Metadata -> {
                            streamingMsg = streamingMsg.copy(
                                latencyMs = event.latencyMs,
                                webSearchUsed = event.webSearchUsed,
                                sources = event.sources
                            )
                        }
                        is AlphaStreamEvent.Completed -> {
                            val finalMsg = event.finalMessage.copy(id = placeholderMsgId)
                            _uiState.update { state ->
                                val list = state.messages.map { m ->
                                    if (m.id == placeholderMsgId) finalMsg else m
                                }
                                state.copy(
                                    messages = list,
                                    alphaState = AlphaState.IDLE
                                )
                            }
                            repository.saveMessages(convoId, _uiState.value.messages)
                        }
                        is AlphaStreamEvent.Failure -> {
                            _uiState.update { state ->
                                state.copy(alphaState = AlphaState.ERROR)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(alphaState = AlphaState.IDLE) }
            }
        }
    }
}
