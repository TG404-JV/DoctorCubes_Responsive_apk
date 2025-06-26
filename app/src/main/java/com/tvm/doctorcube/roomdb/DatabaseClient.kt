package com.tvm.doctorcube.roomdb

import android.content.Context
import androidx.room.Room

object DatabaseClient {
    private var appDatabase: AppDatabase? = null

    @Synchronized
    fun getInstance(context: Context): AppDatabase {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "doctorcube_database"
            ).build()
        }
        return appDatabase!!
    }
}