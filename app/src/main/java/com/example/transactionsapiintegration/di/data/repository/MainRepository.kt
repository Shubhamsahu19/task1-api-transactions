package com.example.transactionsapiintegration.di.data.repository

import com.example.transactionsapiintegration.di.data.ApiHelper
import com.google.gson.JsonObject

class MainRepository (private val apiHelper: ApiHelper) {
    suspend fun login(jsonObject: JsonObject) = apiHelper.login(jsonObject)
    suspend fun getTransaction() = apiHelper.getTransaction()
}