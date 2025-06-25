package edu.ucne.registroticket.presentation.ventas

sealed interface VentasEvent {
    data class VentaChange(val ventaId: Int): VentasEvent
    data class DescripcionChange(val descripcion: String): VentasEvent
    data class MontoChange(val monto: String): VentasEvent
    data object Save: VentasEvent
    data object New: VentasEvent
}