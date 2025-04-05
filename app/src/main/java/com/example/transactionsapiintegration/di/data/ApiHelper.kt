package com.example.transactionsapiintegration.di.data

import com.example.transactionsapiintegration.model.Transaction
import com.google.gson.JsonObject
import retrofit2.Response

interface ApiHelper {
    suspend fun login(jsonObject: JsonObject): Response<JsonObject>
    suspend fun getTransaction(): Response<List<Transaction>>
}