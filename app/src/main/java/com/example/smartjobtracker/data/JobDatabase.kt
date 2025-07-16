package com.example.smartjobtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [JobEntity::class], version = 1, exportSchema = false)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
}