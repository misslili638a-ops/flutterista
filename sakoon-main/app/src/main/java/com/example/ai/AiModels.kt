package com.example.ai

import java.util.UUID

enum class MessageSender {
    USER,
    SAKOON_AI
}

data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val hasError: Boolean = false
)

sealed interface AiChatState {
    data object Idle : AiChatState
    data object Loading : AiChatState
    data class Streaming(val partialResponse: String) : AiChatState
    data class Error(val errorMessage: String) : AiChatState
}
