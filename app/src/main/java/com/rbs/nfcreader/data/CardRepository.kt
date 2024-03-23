package com.rbs.nfcreader.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.rbs.nfcreader.data.model.Card
import com.rbs.nfcreader.data.local.CardDao
import com.rbs.nfcreader.data.local.CardDatabase
import com.rbs.nfcreader.data.network.ApiConfig
import com.rbs.nfcreader.data.network.ApiService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CardRepository(application: Application) {
    private val cardDao: CardDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val apiService: ApiService = ApiConfig.getService()

    init {
        val database = CardDatabase.getDatabase(application)
        cardDao = database.cardDao()
    }

    fun getAllData(): LiveData<List<Card>> = cardDao.getAllData()

    fun insert(card: Card) {
        executorService.execute { cardDao.insert(card) }
    }

    fun delete(id: Int) {
        executorService.execute { cardDao.delete(id) }
    }

    suspend fun send(serialNumber: String, message: String): Card = apiService.sendCardData(serialNumber, message)
}