package com.example.transactionsapiintegration.di.data

import com.example.transactionsapiintegration.model.Transaction
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
 @POST("/login")
 suspend fun login(
  @Body jsonObject: JsonObject
 ) : Response<JsonObject>

 @GET("/transactions")
 suspend fun getTransactions() : Response<List<Transaction>>

}