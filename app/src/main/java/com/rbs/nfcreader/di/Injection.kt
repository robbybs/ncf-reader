package com.rbs.nfcreader.di

import android.content.Context
import com.rbs.nfcreader.data.CardRepository
import com.rbs.nfcreader.data.local.CardDatabase
import com.rbs.nfcreader.data.network.ApiConfig
import com.rbs.nfcreader.utils.AppExecutors

object Injection {
    fun cardRepository(context: Context): CardRepository {
        val apiService = ApiConfig.getService()
        val database = CardDatabase.getDatabase(context)
        val dao = database.cardDao()
        val appExecutors = AppExecutors()

        return CardRepository.getInstance(apiService, dao, appExecutors)
    }
}