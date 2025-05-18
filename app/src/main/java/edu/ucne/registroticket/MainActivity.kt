package edu.ucne.registroticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
        ).fallbackToDestructiveMigration()
            .build()

        setContent {
            RegistroTicketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val navController = rememberNavController()
                        TicketNavHost(navController)
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TicketScreen(
        goTicketList: () -> Unit
    ) {
        var fecha by remember { mutableStateOf("") }
        var prioridadId by remember { mutableStateOf<String?>(null) }
        var cliente by remember { mutableStateOf("") }
        var asunto by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var tecnicoId by remember { mutableStateOf<String?>(null) }
        var errorMessage: String? by remember { mutableStateOf(null) }

        val prioridades = listOf("Alta", "Media", "Baja")
        val tecnicos = listOf("Sebastian", "Marcos", "Esteban")

        var expandedPrioridad by remember { mutableStateOf(false) }
        var expandedTecnico by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Registro Tickets") },
                    navigationIcon = {
                        IconButton(onClick = goTicketList) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Lista"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            label = { Text(text = "Fecha") },
                            value = fecha,
                            onValueChange = { fecha = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedPrioridad,
                            onExpandedChange = { expandedPrioridad = !expandedPrioridad }
                        ) {
                            OutlinedTextField(
                                value = prioridadId ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Prioridad") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedPrioridad,
                                onDismissRequest = { expandedPrioridad = false }
                            ) {
                                prioridades.forEach { prioridad ->
                                    DropdownMenuItem(
                                        text = { Text(prioridad) },
                                        onClick = {
                                            prioridadId = prioridad
                                            expandedPrioridad = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            label = { Text(text = "Cliente") },
                            value = cliente,
                            onValueChange = { cliente = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            label = { Text(text = "Asunto") },
                            value = asunto,
                            onValueChange = { asunto = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            label = { Text(text = "Descripción") },
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedTecnico,
                            onExpandedChange = { expandedTecnico = !expandedTecnico }
                        ) {
                            OutlinedTextField(
                                value = tecnicoId ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Técnico") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTecnico)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedTecnico,
                                onDismissRequest = { expandedTecnico = false }
                            ) {
                                tecnicos.forEach { tecnico ->
                                    DropdownMenuItem(
                                        text = { Text(tecnico) },
                                        onClick = {
                                            tecnicoId = tecnico
                                            expandedTecnico = false
                                        }
                                    )
                                }
                            }
                        }

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = {
                                    fecha = ""
                                    prioridadId = null
                                    cliente = ""
                                    asunto = ""
                                    descripcion = ""
                                    tecnicoId = ""
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo")
                                Text("Nuevo")
                            }

                            val scope = rememberCoroutineScope()

                            OutlinedButton(
                                onClick = {
                                    if (fecha.isBlank() || cliente.isBlank() || asunto.isBlank() || descripcion.isBlank() || prioridadId == null || tecnicoId == null) {
                                        errorMessage = "Por favor complete todos los campos"
                                        return@OutlinedButton
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
                                        goTicketList()
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Done, contentDescription = "Guardar")
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TicketListScreen(
        ticketList: List<TicketEntity>,
        onAddTicket: () -> Unit
    ){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Tickets")
                        }
                    }
                )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = onAddTicket) {
                    Icon(Icons.Filled.Add, "Agregar nueva entidad")
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(it)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ticketList){
                        TicketRow(it)
                    }
                }
            }
        }
    }

    @Composable
    fun TicketRow(ticket: TicketEntity) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ID: ${ticket.ticketId}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                ticket.fecha?.let {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Text(
                    text = ticket.prioridad,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cliente: ${ticket.cliente}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Asunto: ${ticket.asunto}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Descripción: ${ticket.descripcion}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Técnico asignado: ${ticket.tecnico}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
        }
    }


    private suspend fun saveTicket(ticket: TicketEntity) {
        ticketDb.ticketDao().save(ticket)
    }

    @Composable
    fun TicketNavHost(
        navHostController: NavHostController
    ){
        val lifecycleOwner = LocalLifecycleOwner.current
        val ticketList by ticketDb.ticketDao().getAll()
            .collectAsStateWithLifecycle(
                initialValue = emptyList(),
                lifecycleOwner = lifecycleOwner,
                minActiveState = Lifecycle.State.STARTED
            )
        NavHost(
            navController = navHostController,
            startDestination = Screen.TicketList
        ){
            composable<Screen.TicketList>{
                TicketListScreen(
                    ticketList = ticketList,
                    onAddTicket = { navHostController.navigate(Screen.Ticket(0)) }
                )
            }

            composable<Screen.Ticket>{
                TicketScreen(
                    goTicketList = { navHostController.navigate(Screen.TicketList) }
                )
            }
        }
    }


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun Preview() {
        RegistroTicketTheme {
            val ticketList = listOf(
                TicketEntity(
                    ticketId = 1,
                    fecha = "18/5/2025",
                    prioridad = TODO(),
                    cliente = "Alfredo",
                    asunto = "Pasar por el local",
                    descripcion = "Computadora dañada",
                    tecnico = TODO()
                ),
                TicketEntity(
                    ticketId = 2,
                    fecha = "18/5/2025",
                    prioridad = TODO(),
                    cliente = "Rodrigo",
                    asunto = "Pasar por el local",
                    descripcion = "Mala conexion",
                    tecnico = TODO()
                )
            )
        }
    }
}