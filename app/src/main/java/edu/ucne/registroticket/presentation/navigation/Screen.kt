package edu.ucne.registroticket.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object TicketList : Screen()

    @Serializable
    data class Ticket(val ticketId: Int?) : Screen()

    @Serializable
    data class Conversation(val ticketId: Int) : Screen()

    @Serializable
    data object VentaList: Screen()

    @Serializable
    data class Venta(val ventaId: Int?) : Screen()
}