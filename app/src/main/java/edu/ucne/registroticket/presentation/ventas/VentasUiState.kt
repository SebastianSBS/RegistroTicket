package edu.ucne.registroticket.presentation.ventas

import edu.ucne.registroticket.data.remote.dto.VentaDto
import java.math.BigDecimal

data class VentasUiState(
    val ventaId: Int = 0,
    val descripcion: String = "",
    val monto: BigDecimal = BigDecimal.ZERO,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val venta: VentaDto = VentaDto(),
    val ventas: List<VentaDto> = emptyList()
)