package com.tvm.doctorcube.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tvm.doctorcube.home.model.UpcomingEvent

@Database(entities = [UpcomingEvent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun upcomingEventDao(): UpcomingEventDao
}