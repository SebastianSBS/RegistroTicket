package edu.ucne.registroticket.presentation.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroticket.data.remote.Resource
import edu.ucne.registroticket.data.remote.dto.VentaDto
import edu.ucne.registroticket.data.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val ventaRepository: VentaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VentasUiState())
    val state = _state.asStateFlow()

    private fun getVentas() {
        viewModelScope.launch {
            ventaRepository.getVentas().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(ventas = result.data ?: emptyList(), isLoading = false)
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(errorMessage = "Ha ocurrido un error, intente de nuevo..", isLoading = false)
                        }
                    }
                }
            }
        }
    }

    fun postVentas() {
        val currentVenta = _state.value.venta

        if (currentVenta.descripcion.isNullOrBlank()) {
            _state.update {
                it.copy(errorMessage = "La descripción es requerida")
            }
            return
        }

        if (currentVenta.monto == null || currentVenta.monto <= 0) {
            _state.update {
                it.copy(errorMessage = "El monto debe ser mayor a 0")
            }
            return
        }

        viewModelScope.launch {
            try {
                ventaRepository.postVentas(currentVenta).collectLatest { result ->
                    when(result){
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }

                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    successMessage = "Guardado correctamente",
                                    isLoading = false
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    errorMessage = result.message ?: "Ocurrio un error, intentelo de nuevo",
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = "Error de conexión: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteVenta(){
        viewModelScope.launch {
            ventaRepository.deleteVenta(_state.value.venta.ventaId).collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                successMessage = "Eliminado correctamente",
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                errorMessage = "Ocurrio un error, intentelo de nuevo",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun getVentaById(ventaId: Int) {
        viewModelScope.launch {
            ventaRepository.getVentaById(ventaId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                venta = result.data ?: VentaDto(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                errorMessage = "Ocurrio un error, intentelo de nuevo",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }


    fun onEvent(event: VentasEvent) {
        when (event) {
            is VentasEvent.VentaChange -> {
                _state.update {
                    it.copy(
                        venta = it.venta.copy(ventaId = event.ventaId)
                    )
                }
            }


            is VentasEvent.DescripcionChange -> {
                _state.update {
                    it.copy(
                        venta = it.venta.copy(descripcion = event.descripcion)
                    )
                }
            }

            is VentasEvent.MontoChange -> {
                _state.update {
                    it.copy(
                        venta = it.venta.copy(monto = event.monto)
                    )
                }
            }

            VentasEvent.Save -> {
                postVentas()
            }

            VentasEvent.New -> {
                _state.update {
                    it.copy(
                        venta = VentaDto(),
                        successMessage = null,
                        errorMessage = "",
                        isLoading = false
                    )
                }
            }

            VentasEvent.Delete -> {
                deleteVenta()
            }

            VentasEvent.FindById -> {
                getVentaById(_state.value.venta.ventaId ?: 0)
            }

            VentasEvent.LoadVentas -> {
                getVentas()
            }
        }
    }
}