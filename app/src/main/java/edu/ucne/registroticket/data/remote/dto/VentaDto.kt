package edu.ucne.registroticket.data.remote.dto

import java.math.BigDecimal

data class VentaDto (
    val ventaId : Int? = null,
    val descripcion : String = "",
    val monto : BigDecimal = BigDecimal.ZERO
)

