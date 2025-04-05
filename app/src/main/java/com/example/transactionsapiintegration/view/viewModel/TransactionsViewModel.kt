package com.example.transactionsapiintegration.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dictatenow.androidapp.utils.NetworkHelper
import com.dictatenow.androidapp.utils.Resource
import com.example.transactionsapiintegration.di.data.repository.MainRepository
import com.example.transactionsapiintegration.model.Transaction
import com.example.transactionsapiintegration.utils.Constants
import com.google.gson.JsonObject
import org.json.JSONObject

class TransactionsViewModel  (
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _transactionData = MutableLiveData<Resource<List<Transaction>>>()
    val transactionData: LiveData<Resource<List<Transaction>>>
        get() = _transactionData

    suspend fun getTransaction() {
        _transactionData.postValue(Resource.loading(null))
        if (networkHelper.isNetworkConnected()) {
            mainRepository.getTransaction().let {
                if (it.isSuccessful) {
                    _transactionData.postValue(Resource.success(it.body()))
                } else {
//                    val errorBodyString = it.errorBody()?.string()
//                    // Parse the JSON content to extract the error message
//                    val jsonObjectError = JSONObject(errorBodyString!!)
//                    val errorMessage = jsonObjectError.getString("message")
//                    _transactionData.postValue(Resource.error(errorMessage, null))
                }
            }


        } else {
            _transactionData.postValue(Resource.error("No internet connection", null))
        }
    }

}