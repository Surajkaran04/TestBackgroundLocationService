package com.fort.testbackgroundlocationservice.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EntityLocation::class], version = 1,exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDAO(): LocationDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
        
        private fun buildDatabase(appContext: Context) = Room.databaseBuilder(appContext, AppDatabase::class.java, "test_background_location")
            .fallbackToDestructiveMigration().build()
    }
}