package com.example.data

import android.content.Context
import com.example.model.ChatMessage
import com.example.model.Conversation
import com.example.model.MessageRole
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Robust JSON-backed repository for persistent conversations and chat messages.
 * Automatically saves on updates and loads smoothly on launch.
 */
class ConversationRepository(private val context: Context) {

    private val conversationsFile = File(context.filesDir, "ahadu_conversations.json")
    private val messagesDir = File(context.filesDir, "ahadu_messages").apply { mkdirs() }

    fun loadConversations(): List<Conversation> {
        if (!conversationsFile.exists()) {
            return getDefaultConversations()
        }
        return try {
            val jsonStr = conversationsFile.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Conversation>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Conversation(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        preview = obj.optString("preview", ""),
                        updatedAt = obj.getLong("updatedAt"),
                        messagesCount = obj.optInt("messagesCount", 0),
                        isPinned = obj.optBoolean("isPinned", false)
                    )
                )
            }
            if (list.isEmpty()) getDefaultConversations() else list.sortedByDescending { it.updatedAt }
        } catch (e: Exception) {
            getDefaultConversations()
        }
    }

    fun saveConversations(conversations: List<Conversation>) {
        try {
            val array = JSONArray()
            for (c in conversations) {
                val obj = JSONObject().apply {
                    put("id", c.id)
                    put("title", c.title)
                    put("preview", c.preview)
                    put("updatedAt", c.updatedAt)
                    put("messagesCount", c.messagesCount)
                    put("isPinned", c.isPinned)
                }
                array.put(obj)
            }
            conversationsFile.writeText(array.toString())
        } catch (_: Exception) {}
    }

    fun loadMessages(conversationId: String): List<ChatMessage> {
        val file = File(messagesDir, "$conversationId.json")
        if (!file.exists()) {
            return if (conversationId == "intro_convo") getDefaultMessages() else emptyList()
        }
        return try {
            val jsonStr = file.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ChatMessage>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val roleStr = obj.optString("role", "USER")
                val role = try { MessageRole.valueOf(roleStr) } catch (_: Exception) { MessageRole.USER }
                list.add(
                    ChatMessage(
                        id = obj.getString("id"),
                        role = role,
                        content = obj.getString("content"),
                        timestamp = obj.getLong("timestamp"),
                        webSearchUsed = obj.optBoolean("webSearchUsed", false),
                        latencyMs = if (obj.has("latencyMs")) obj.getLong("latencyMs") else null,
                        modelName = obj.optString("modelName", "ALPHA-v1.4 Neural")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMessages(conversationId: String, messages: List<ChatMessage>) {
        try {
            val file = File(messagesDir, "$conversationId.json")
            val array = JSONArray()
            for (m in messages) {
                val obj = JSONObject().apply {
                    put("id", m.id)
                    put("role", m.role.name)
                    put("content", m.content)
                    put("timestamp", m.timestamp)
                    put("webSearchUsed", m.webSearchUsed)
                    m.latencyMs?.let { put("latencyMs", it) }
                    put("modelName", m.modelName)
                }
                array.put(obj)
            }
            file.writeText(array.toString())
        } catch (_: Exception) {}
    }

    fun deleteConversation(conversationId: String) {
        val file = File(messagesDir, "$conversationId.json")
        if (file.exists()) file.delete()
    }

    private fun getDefaultConversations(): List<Conversation> {
        val now = System.currentTimeMillis()
        val defaultList = listOf(
            Conversation(
                id = "intro_convo",
                title = "ALPHA Neural Protocol Overview",
                preview = "I am AHADU, an intelligent conversational neural platform...",
                updatedAt = now,
                messagesCount = 2,
                isPinned = true
            ),
            Conversation(
                id = "quantum_computing",
                title = "Quantum State Superposition Algorithms",
                preview = "Exploring basis states and decoherence dynamics in quantum neural meshes...",
                updatedAt = now - 3600_000 * 4,
                messagesCount = 4
            ),
            Conversation(
                id = "code_opt",
                title = "Zero-Allocation Concurrent Buffer in Kotlin",
                preview = "Ring buffer optimization with zero runtime heap spikes...",
                updatedAt = now - 86400_000 * 2,
                messagesCount = 6
            )
        )
        saveConversations(defaultList)
        return defaultList
    }

    private fun getDefaultMessages(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "m1",
                role = MessageRole.USER,
                content = "Initialize AHADU AI operational diagnostics and report ALPHA status.",
                timestamp = System.currentTimeMillis() - 60_000
            ),
            ChatMessage(
                id = "m2",
                role = MessageRole.ASSISTANT,
                content = """
                    **AHADU Online. ALPHA Neural Core operational.**

                    All primary tensor matrices are stabilized at sub-surface nodes:
                    - **Model Signature**: `ALPHA-v1.4 Neural Tensor Mesh`
                    - **Inference Latency**: 214ms nominal
                    - **Multi-modal Sensorium**: Ready for text, code, audio synthesis, and document comprehension.

                    Aether Industries welcome protocol engaged. What objective shall we advance?
                """.trimIndent(),
                timestamp = System.currentTimeMillis() - 30_000,
                latencyMs = 214,
                modelName = "ALPHA-v1.4 Neural"
            )
        )
    }
}
