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
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.text.toBigDecimalOrNull

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val ventaRepository: VentaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VentasUiState())
    val state = _state.asStateFlow()

    init {
        getVentas()
    }

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
        viewModelScope.launch {
            ventaRepository.postVentas(_state.value.venta).collectLatest { result ->
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
                                errorMessage = "Ocurrio un error, intentelo de nuevo",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }

        fun onEvent(event: VentasEvent) {
            when (event) {
                is VentasEvent.DescripcionChange -> {
                    _state.update {
                        it.copy(
                            venta = it.venta.copy(descripcion = event.descripcion)
                        )
                    }
                }

                is VentasEvent.MontoChange -> {
                    val monto = try {
                        BigDecimal(event.monto)
                    } catch (e: Exception) {
                        BigDecimal.ZERO
                    }

                    _state.update {
                        it.copy(
                            venta = it.venta.copy(monto = monto)
                        )
                    }
                }

                VentasEvent.Save -> {
                    postVentas()
                    onEvent(VentasEvent.New)
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
            }
        }

    }
}