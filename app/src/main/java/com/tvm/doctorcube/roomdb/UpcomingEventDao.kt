package com.tvm.doctorcube.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvm.doctorcube.home.model.UpcomingEvent

@Dao
interface UpcomingEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<UpcomingEvent>)

    @Query("SELECT * FROM upcomingevent")
    fun getAllEvents(): LiveData<List<UpcomingEvent>>

    @Query("DELETE FROM upcomingevent WHERE id = :eventId")
    suspend fun deleteEvent(eventId: String)

    @Query("DELETE FROM upcomingevent")
    suspend fun clearAll()
}