package edu.ucne.registroticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import edu.ucne.registroticket.data.local.Database.TicketDb
import edu.ucne.registroticket.data.local.Entities.TicketEntity
import edu.ucne.registroticket.presentation.navigation.Screen
import edu.ucne.registroticket.ui.theme.RegistroTicketTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var ticketDb: TicketDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ticketDb = Room.databaseBuilder(
            applicationContext,
            TicketDb::class.java,
            "Ticket.db"
        ).fallbackToDestructiveMigration().build()

        setContent {
            RegistroTicketTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    TicketNavHost(navController)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TicketScreen(goTicketList: () -> Unit) {
        var fecha by remember { mutableStateOf("") }
        var prioridadId by remember { mutableStateOf<String?>(null) }
        var cliente by remember { mutableStateOf("") }
        var asunto by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var tecnicoId by remember { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val prioridades = listOf("Alta", "Media", "Baja")
        val tecnicos = listOf("Sebastian", "Marcos", "Esteban")
        var expandedPrioridad by remember { mutableStateOf(false) }
        var expandedTecnico by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Nuevo Ticket",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = goTicketList) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Formulario de Registro",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField("Fecha", fecha) { fecha = it }
                        CustomDropdownMenu("Prioridad", prioridadId, expandedPrioridad, prioridades,
                            onToggleExpand = { expandedPrioridad = !expandedPrioridad },
                            onSelectItem = {
                                prioridadId = it
                                expandedPrioridad = false
                            })
                        CustomTextField("Cliente", cliente) { cliente = it }
                        CustomTextField("Asunto", asunto) { asunto = it }
                        CustomTextField("Descripción", descripcion) { descripcion = it }
                        CustomDropdownMenu("Técnico", tecnicoId, expandedTecnico, tecnicos,
                            onToggleExpand = { expandedTecnico = !expandedTecnico },
                            onSelectItem = {
                                tecnicoId = it
                                expandedTecnico = false
                            })

                        AnimatedVisibility(visible = errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FilledTonalButton(onClick = {
                                fecha = ""
                                prioridadId = null
                                cliente = ""
                                asunto = ""
                                descripcion = ""
                                tecnicoId = null
                                errorMessage = null
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nuevo")
                            }

                            FilledTonalButton(onClick = {
                                if (fecha.isBlank() || cliente.isBlank() || asunto.isBlank() || descripcion.isBlank() || prioridadId == null || tecnicoId == null) {
                                    errorMessage = "Por favor complete todos los campos"
                                    return@FilledTonalButton
                                }
                                scope.launch {
                                    saveTicket(
                                        TicketEntity(
                                            fecha = fecha,
                                            prioridad = prioridadId!!,
                                            cliente = cliente,
                                            asunto = asunto,
                                            descripcion = descripcion,
                                            tecnico = tecnicoId!!
                                        )
                                    )
                                    fecha = ""
                                    prioridadId = null
                                    cliente = ""
                                    asunto = ""
                                    descripcion = ""
                                    tecnicoId = null
                                    errorMessage = null
                                    goTicketList()
                                }
                            }) {
                                Icon(Icons.Default.Done, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CustomDropdownMenu(
        label: String,
        selectedValue: String?,
        expanded: Boolean,
        items: List<String>,
        onToggleExpand: () -> Unit,
        onSelectItem: (String) -> Unit
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onToggleExpand() }) {
            OutlinedTextField(
                value = selectedValue ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onToggleExpand() }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = { onSelectItem(item) }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TicketListScreen(ticketList: List<TicketEntity>, onAddTicket: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(
                        text = "Listado de Tickets",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddTicket) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar nuevo ticket")
                }
            }
        ) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ticketList) { ticket -> TicketRow(ticket) }
            }
        }
    }

    @Composable
    fun TicketRow(ticket: TicketEntity) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${ticket.ticketId}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    ticket.fecha?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prioridad: ${ticket.prioridad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cliente: ${ticket.cliente}", style = MaterialTheme.typography.titleMedium)
                Text("Asunto: ${ticket.asunto}", style = MaterialTheme.typography.bodyMedium)
                Text("Descripción: ${ticket.descripcion}", style = MaterialTheme.typography.bodySmall)
                Text("Técnico asignado: ${ticket.tecnico}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    private suspend fun saveTicket(ticket: TicketEntity) {
        ticketDb.ticketDao().save(ticket)
    }

    @Composable
    fun TicketNavHost(navHostController: NavHostController) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val ticketList by ticketDb.ticketDao().getAll()
            .collectAsStateWithLifecycle(
                initialValue = emptyList(),
                lifecycleOwner = lifecycleOwner,
                minActiveState = Lifecycle.State.STARTED
            )
        NavHost(navController = navHostController, startDestination = Screen.TicketList) {
            composable<Screen.TicketList> {
                TicketListScreen(ticketList = ticketList, onAddTicket = {
                    navHostController.navigate(Screen.Ticket(0))
                })
            }
            composable<Screen.Ticket> {
                TicketScreen(goTicketList = {
                    navHostController.navigate(Screen.TicketList)
                })
            }
        }
    }
}
