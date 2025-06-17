package edu.ucne.registroticket.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Conversation")
data class ConversationEntity (
    @PrimaryKey
    val conversationId: Int? = null,
    val ticketId: Int,
    val contenido: String,
    val tipo: String,
    val fecha: String,
    val hora: String
)