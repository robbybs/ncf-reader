package com.rbs.nfcreader.data.network

import com.rbs.nfcreader.data.model.Card
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("nfc")
    suspend fun sendCardData(
        @Query("sn") serialNumber: String,
        @Query("message") message: String
    ): Card
}