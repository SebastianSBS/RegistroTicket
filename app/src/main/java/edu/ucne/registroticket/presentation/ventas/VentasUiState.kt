package edu.ucne.registroticket.presentation.ventas

import edu.ucne.registroticket.data.remote.dto.VentaDto

data class VentasUiState(
    val ventaId: Int = 0,
    val descripcion: String = "",
    val monto: Double = 0.0,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val venta: VentaDto = VentaDto(),
    val ventas: List<VentaDto> = emptyList()
)