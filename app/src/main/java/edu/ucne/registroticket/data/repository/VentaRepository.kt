package edu.ucne.registroticket.data.repository

import retrofit2.HttpException
import edu.ucne.registroticket.data.remote.Resource
import edu.ucne.registroticket.data.remote.VentasAPI
import edu.ucne.registroticket.data.remote.dto.VentaDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VentaRepository @Inject constructor(
    private val ventasAPI: VentasAPI
) {
    fun getVentas(): Flow<Resource<List<VentaDto>>> = flow {
        try {
            emit(Resource.Loading())

            val ventas = ventasAPI.getVentas()

            emit(Resource.Success(ventas))

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "Error al conectarse con la API"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado, verificar tu conexion a internet"))
        }
    }

    fun getVentaById(ventaId: Int): Flow<Resource<VentaDto>> = flow {
        try {
            emit(Resource.Loading())

            val ventas = ventasAPI.getVentaById(ventaId)

            emit(Resource.Success(ventas))

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "Error al conectarse con la API"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado, verificar tu conexion a internet"))
        }
    }

    fun postVentas(ventaDto: VentaDto): Flow<Resource<VentaDto>> = flow{
        try {
            emit(Resource.Loading())

            ventasAPI.postVentas(ventaDto)
            emit(Resource.Success(ventaDto))

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "Error al conectarse con la API"))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado, verificar tu conexion a internet"))
        }
    }



}