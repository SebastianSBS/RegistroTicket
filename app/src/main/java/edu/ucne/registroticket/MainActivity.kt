package edu.ucne.registroticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import edu.ucne.registroticket.data.local.Database.TicketDb
import edu.ucne.registroticket.data.local.Entities.ConversationEntity
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
        ticketId: Int? = null,
        goTicketList: () -> Unit
    ) {
        var fecha by remember { mutableStateOf("") }
        var prioridadId by remember { mutableStateOf<String?>(null) }
        var cliente by remember { mutableStateOf("") }
        var asunto by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var tecnicoId by remember { mutableStateOf<String?>(null) }
        var errorMessage: String? by remember { mutableStateOf(null) }
        var currentTicket by remember { mutableStateOf<TicketEntity?>(null) }

        val prioridades = listOf("Alta", "Media", "Baja")
        val tecnicos = listOf("Sebastian", "Marcos", "Esteban")
        var expandedPrioridad by remember { mutableStateOf(false) }
        var expandedTecnico by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()
        val isEditing = ticketId != null && ticketId > 0

        LaunchedEffect (ticketId) {
            if (isEditing && ticketId != null) {
                try {
                    val ticket = ticketDb.ticketDao().find(ticketId)
                    ticket?.let {
                        currentTicket = it
                        fecha = it.fecha ?: ""
                        prioridadId = it.prioridad
                        cliente = it.cliente ?: ""
                        asunto = it.asunto ?: ""
                        descripcion = it.descripcion ?: ""
                        tecnicoId = it.tecnico
                    }
                } catch (e: Exception) {
                    errorMessage = "Error al cargar el ticket: ${e.message}"
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (isEditing) "Editar Ticket" else "Nuevo Ticket",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = goTicketList) {
                            Icon(Icons.Default.Menu, contentDescription = "Ir a lista")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    if (isEditing) "Formulario de Edición" else "Formulario de Registro",
                    style = MaterialTheme.typography.headlineSmall
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CustomTextField("Fecha", fecha) { fecha = it }

                        CustomDropdownMenu(
                            label = "Prioridad",
                            selectedValue = prioridadId,
                            expanded = expandedPrioridad,
                            items = prioridades,
                            onToggleExpand = { expandedPrioridad = !expandedPrioridad },
                            onSelectItem = {
                                prioridadId = it
                                expandedPrioridad = false
                            }
                        )

                        CustomTextField("Cliente", cliente) { cliente = it }
                        CustomTextField("Asunto", asunto) { asunto = it }
                        CustomTextField("Descripción", descripcion) { descripcion = it }

                        CustomDropdownMenu(
                            label = "Técnico",
                            selectedValue = tecnicoId,
                            expanded = expandedTecnico,
                            items = tecnicos,
                            onToggleExpand = { expandedTecnico = !expandedTecnico },
                            onSelectItem = {
                                tecnicoId = it
                                expandedTecnico = false
                            }
                        )

                        errorMessage?.let {
                            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    fecha = ""
                                    prioridadId = null
                                    cliente = ""
                                    asunto = ""
                                    descripcion = ""
                                    tecnicoId = null
                                    errorMessage = null
                                    currentTicket = null
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nuevo")
                            }

                            FilledTonalButton(
                                onClick = {
                                    if (fecha.isBlank() || cliente.isBlank() || asunto.isBlank() ||
                                        descripcion.isBlank() || prioridadId == null || tecnicoId == null) {
                                        errorMessage = "Por favor complete todos los campos"
                                        return@FilledTonalButton
                                    }

                                    scope.launch {
                                        try {
                                            if (isEditing && currentTicket != null) {
                                                val updatedTicket = currentTicket!!.copy(
                                                    fecha = fecha,
                                                    prioridad = prioridadId!!,
                                                    cliente = cliente,
                                                    asunto = asunto,
                                                    descripcion = descripcion,
                                                    tecnico = tecnicoId!!
                                                )
                                                ticketDb.ticketDao().update(updatedTicket)
                                            } else {
                                                val newTicket = TicketEntity(
                                                    fecha = fecha,
                                                    prioridad = prioridadId!!,
                                                    cliente = cliente,
                                                    asunto = asunto,
                                                    descripcion = descripcion,
                                                    tecnico = tecnicoId!!
                                                )
                                                ticketDb.ticketDao().save(newTicket)
                                            }

                                            fecha = ""
                                            prioridadId = null
                                            cliente = ""
                                            asunto = ""
                                            descripcion = ""
                                            tecnicoId = null
                                            errorMessage = null
                                            goTicketList()
                                        } catch (e: Exception) {
                                            errorMessage = "Error al guardar: ${e.message}"
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isEditing) "Actualizar" else "Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConversationScreen(
        ticketId: Int,
        goBack: () -> Unit
    ) {
        var mensaje by remember { mutableStateOf("") }
        var tipoSeleccionado by remember { mutableStateOf("operator") }
        val scope = rememberCoroutineScope()

        val lifecycleOwner = LocalLifecycleOwner.current
        val conversaciones by ticketDb.ticketDao().getConversacionesByTicket(ticketId)
            .collectAsStateWithLifecycle(
                initialValue = emptyList(),
                lifecycleOwner = lifecycleOwner,
                minActiveState = Lifecycle.State.STARTED
            )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Conversación - Ticket #$ticketId") },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conversaciones) { conversation ->
                        ConversacionItem(conversation = conversation)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                onClick = { tipoSeleccionado = "operator" },
                                label = { Text("Operator") },
                                selected = tipoSeleccionado == "operator"
                            )
                            FilterChip(
                                onClick = { tipoSeleccionado = "owner" },
                                label = { Text("Owner") },
                                selected = tipoSeleccionado == "owner"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = mensaje,
                                onValueChange = { mensaje = it },
                                label = { Text("Mensaje") },
                                modifier = Modifier.weight(1f)
                            )

                            Button (
                                onClick = {
                                    if (mensaje.isNotBlank()) {
                                        scope.launch {
                                            val nuevaConversacion = ConversationEntity(
                                                ticketId = ticketId,
                                                contenido = mensaje,
                                                tipo = tipoSeleccionado,
                                                fecha = "25/05/2025",
                                                hora = "00:00"
                                            )
                                            ticketDb.ticketDao().insertConversation(nuevaConversacion)
                                            mensaje = ""
                                        }
                                    }
                                }
                            ) {
                                Text("Enviar")
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
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
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onToggleExpand() }
        ) {
            OutlinedTextField(
                value = selectedValue ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggleExpand() }
            ) {
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
    fun TicketListScreen(
        ticketList: List<TicketEntity>,
        onAddTicket: () -> Unit,
        onEditTicket: (Int) -> Unit,
        onChatTicket: (Int) -> Unit
    ) {
        val scope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Listado de Tickets",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                )
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
                items(ticketList) { ticket ->
                    TicketRow(
                        ticket = ticket,
                        onEdit = {
                            onEditTicket(ticket.ticketId ?: 0)
                        },
                        onDelete = { t ->
                            scope.launch {
                                deleteTicket(t)
                            }
                        },
                        onChat = {
                            onChatTicket(ticket.ticketId ?: 0)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun TicketRow(
        ticket: TicketEntity,
        onEdit: (TicketEntity) -> Unit,
        onDelete: (TicketEntity) -> Unit,
        onChat: (TicketEntity) -> Unit
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
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
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onChat(ticket) }) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Ver Conversación",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { onEdit(ticket) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Ticket",
                            tint = Color.DarkGray
                        )
                    }

                    IconButton(onClick = { onDelete(ticket) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Ticket",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ConversacionItem(conversation: ConversationEntity) {
        val isOperator = conversation.tipo == "operator"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isOperator) Arrangement.Start else Arrangement.End
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(vertical = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOperator)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isOperator) 4.dp else 16.dp,
                    bottomEnd = if (isOperator) 16.dp else 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOperator) "Operator" else "Owner",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOperator)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${conversation.fecha} ${conversation.hora}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = conversation.contenido,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isOperator)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }


    private suspend fun saveTicket(ticket: TicketEntity) {
        ticketDb.ticketDao().save(ticket)
    }

    private suspend fun deleteTicket(ticket: TicketEntity){
        ticketDb.ticketDao().delete(ticket)
    }

    private suspend fun editTicket(ticket: TicketEntity){
        ticketDb.ticketDao().update(ticket)
    }

    @Composable
    fun TicketNavHost(
        navHostController: NavHostController
    ) {
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
        ) {
            composable<Screen.TicketList> {
                TicketListScreen(
                    ticketList = ticketList,
                    onAddTicket = {
                        navHostController.navigate(Screen.Ticket(ticketId = null))
                    },
                    onEditTicket = { ticketId ->
                        navHostController.navigate(Screen.Ticket(ticketId = ticketId))
                    },
                    onChatTicket = { ticketId ->  // Agrega este bloque
                        navHostController.navigate(Screen.Conversation(ticketId = ticketId))
                    }
                )
            }

            composable<Screen.Ticket> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.Ticket>()
                TicketScreen(
                    ticketId = args.ticketId,
                    goTicketList = {
                        navHostController.navigate(Screen.TicketList) {
                            popUpTo(Screen.TicketList) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.Conversation> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.Conversation>()
                ConversationScreen(
                    ticketId = args.ticketId,
                    goBack = {
                        navHostController.popBackStack()
                    }
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