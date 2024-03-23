package com.rbs.nfcreader.data

import androidx.lifecycle.LiveData
import com.rbs.nfcreader.data.local.CardDao
import com.rbs.nfcreader.data.model.Card
import com.rbs.nfcreader.data.network.ApiService
import com.rbs.nfcreader.utils.AppExecutors

class CardRepository(
    private val apiService: ApiService,
    private val cardDao: CardDao,
    private val appExecutors: AppExecutors
) {

    fun getAllData(): LiveData<List<Card>> = cardDao.getAllData()

    fun insert(card: Card) {
        appExecutors.diskIO.execute { cardDao.insert(card) }
    }

    fun delete(id: Int) {
        appExecutors.diskIO.execute { cardDao.delete(id) }
    }

    suspend fun send(serialNumber: String, message: String): Card =
        apiService.sendCardData(serialNumber, message)

    companion object {
        @Volatile
        private var instance: CardRepository? = null
        fun getInstance(
            apiService: ApiService,
            cardDao: CardDao,
            appExecutors: AppExecutors
        ): CardRepository =
            instance ?: synchronized(this) {
                instance ?: CardRepository(apiService, cardDao, appExecutors)
            }.also { instance = it }
    }
}