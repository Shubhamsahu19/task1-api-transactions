package com.example.transactionsapiintegration.model

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}