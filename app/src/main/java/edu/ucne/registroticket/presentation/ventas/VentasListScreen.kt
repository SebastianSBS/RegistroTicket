package edu.ucne.registroticket.presentation.ventas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroticket.data.remote.dto.VentaDto
import androidx.compose.runtime.getValue

@Composable
fun VentaListScreen(
    viewModel: VentaViewModel = hiltViewModel(),
    goToVenta: (Int) -> Unit,
    createVenta: () -> Unit
){
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect (Unit) {
        viewModel.onEvent(VentasEvent.LoadVentas)
    }

    VentaListBodyScreen(
        uiState = uiState,
        goToVenta,
        createVenta
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaListBodyScreen(
    uiState: VentasUiState,
    goToVenta: (Int) -> Unit,
    createVenta: () -> Unit
) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lista de ventas",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton (onClick = createVenta) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar nueva venta")
            }
        }
    ) { padding ->
        LazyColumn (
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items (uiState.ventas) {
                VentaRow(
                    it,
                    goToVenta,
                    createVenta
                )
            }
        }
    }
}

@Composable
fun VentaRow(
    it: VentaDto,
    goToVenta: (Int) -> Unit,
    createVenta: () -> Unit
){
    Card (
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ){
        Column (modifier = Modifier.padding(16.dp)) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable() {
                    goToVenta  (it.ventaId ?: 0)
                }
            ) {
                Text(
                    text = "ID: ${it.ventaId}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Descripcion: ${it.descripcion}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Monto: ${it.monto}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    HorizontalDivider()
}