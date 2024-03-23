package com.rbs.nfcreader.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rbs.nfcreader.data.model.Card

@Database(entities = [Card::class], version = 1)
abstract class CardDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): CardDatabase {
            if (INSTANCE == null) {
                synchronized(CardDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CardDatabase::class.java, "card_database")
                        .build()
                }
            }
            return INSTANCE as CardDatabase
        }
    }
}