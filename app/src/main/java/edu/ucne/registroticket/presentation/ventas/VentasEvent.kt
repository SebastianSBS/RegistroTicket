package edu.ucne.registroticket.presentation.ventas

sealed interface VentasEvent {
    data class VentaChange(val ventaId: Int): VentasEvent
    data class DescripcionChange(val descripcion: String): VentasEvent
    data class MontoChange(val monto: Double): VentasEvent
    data object Save: VentasEvent
    data object Delete: VentasEvent
    data object New: VentasEvent
    data object FindById: VentasEvent
    data object LoadVentas : VentasEvent
}