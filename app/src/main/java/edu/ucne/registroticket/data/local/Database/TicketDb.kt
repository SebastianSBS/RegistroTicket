package edu.ucne.registroticket.data.local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.registroticket.data.local.Dao.TicketDao
import edu.ucne.registroticket.data.local.Entities.TicketEntity

@Database(
    entities = [
        TicketEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TicketDb : RoomDatabase(){
    abstract fun ticketDao(): TicketDao
}