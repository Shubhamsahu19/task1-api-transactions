package com.example.transactionsapiintegration.model
data class Transaction(
    val id: Int,
    val date: String,
    val amount: Int,
    val category: String,
    val description: String
)
