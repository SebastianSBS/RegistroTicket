package edu.ucne.registroticket.data.local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.registroticket.data.local.Dao.TicketDao
import edu.ucne.registroticket.data.local.Entities.ConversationEntity
import edu.ucne.registroticket.data.local.Entities.TicketEntity

@Database(
    entities = [
        TicketEntity::class,
        ConversationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TicketDb : RoomDatabase(){
    abstract fun ticketDao(): TicketDao
}