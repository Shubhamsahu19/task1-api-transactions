package com.example.transactionsapiintegration.di.data

import com.example.transactionsapiintegration.model.Transaction
import com.google.gson.JsonObject
import retrofit2.Response


class ApiHelperImpl(private val apiServices: ApiService) : ApiHelper {
    override suspend fun login(jsonObject: JsonObject): Response<JsonObject> = apiServices.login(jsonObject)
    override suspend fun getTransaction(): Response<List<Transaction>> = apiServices.getTransactions()
}