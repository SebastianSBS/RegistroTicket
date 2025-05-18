package edu.ucne.registroticket.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import edu.ucne.registroticket.data.local.Entities.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Upsert()
    suspend fun save(ticket: TicketEntity)

    @Insert
    suspend fun insert(ticket: TicketEntity)

    @Update
    suspend fun update(ticket: TicketEntity)

    @Query(
        """
            SELECT * 
            FROM Tickets
            WHERE ticketId=:id
            LIMIT 1
        """
    )

    suspend fun find(id: Int): TicketEntity?

    @Delete
    suspend fun delete(ticket: TicketEntity)

    @Query("SELECT * FROM Tickets")
    fun getAll(): Flow<List<TicketEntity>>
}