package edu.ucne.registroticket.data.remote

import edu.ucne.registroticket.data.remote.dto.VentaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface VentasAPI {
    @GET("api/Ventas")
    @Headers("X-API-Key: test")
    suspend fun getVentas():List<VentaDto>

    @GET("api/Ventas/{id}")
    @Headers("X-API-Key: test")
    suspend fun getVentaById(id: Int): VentaDto

    @POST("api/Ventas")
    @Headers("X-API-Key: test")
    suspend fun postVentas(@Body ventas: VentaDto): VentaDto
}