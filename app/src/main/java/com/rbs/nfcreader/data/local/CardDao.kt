package com.rbs.nfcreader.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rbs.nfcreader.data.model.Card

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(card: Card)

    @Query("DELETE FROM card WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * from card")
    fun getAllData(): LiveData<List<Card>>
}