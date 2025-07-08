package edu.ucne.registroticket.presentation.ventas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VentasScreen(
    viewModel: VentaViewModel = hiltViewModel(),
    VentaId: Int?,
    goBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val venta = state.venta

    LaunchedEffect (state.successMessage) {
        if (state.successMessage != null) {
            kotlinx.coroutines.delay(1500)
            goBack()
        }
    }

    Card (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Text(
                    text = "Registro de Ventas",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer invisible para balancear el diseÃ±o
                Spacer(modifier = Modifier.width(48.dp))
            }

            state.successMessage?.let {
                Text(text = it)
            }

            state.errorMessage?.let{
                Text(text = it)
            }

            OutlinedTextField(
                value = venta.descripcion,
                onValueChange = { viewModel.onEvent(VentasEvent.DescripcionChange(it)) },
                label = { Text(text = "Descripcion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),

                )


            OutlinedTextField(
                value = venta.monto.toString(),
                onValueChange = {
                    it.toDoubleOrNull()?.let { monto ->
                        viewModel.onEvent(VentasEvent.MontoChange(monto))
                    }
                },
                label = { Text(text = "Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button (
                    onClick = {
                        viewModel.onEvent(VentasEvent.Save)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Guardar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Guardar")
                    }
                }

                Button(
                    onClick = {
                        viewModel.onEvent(VentasEvent.New)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Limpiar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Limpiar")
                    }
                }
            }
        }
    }
}
