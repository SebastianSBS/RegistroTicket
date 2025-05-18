package edu.ucne.registroticket.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object TicketList : Screen()

    @Serializable
    data class Ticket(val ticketId: Int?) : Screen()
}