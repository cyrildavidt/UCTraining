package com.sib.apps.sparklex.uctraining.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudentEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {


                val roomBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                   "student.db"
                )
                roomBuilder.allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                val instance = roomBuilder.build()
                INSTANCE = instance
                instance
            }
    }

}