package edu.ucne.registroticket.presentation.ventas

import java.math.BigDecimal

sealed interface VentasEvent {
    data class VentaChange(val ventaId: Int): VentasEvent
    data class DescripcionChange(val descripcion: String): VentasEvent
    data class MontoChange(val monto: BigDecimal): VentasEvent
    data object Save: VentasEvent
    data object New: VentasEvent
}