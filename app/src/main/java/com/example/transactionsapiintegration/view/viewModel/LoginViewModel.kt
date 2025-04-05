package com.example.transactionsapiintegration.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dictatenow.androidapp.utils.NetworkHelper
import com.dictatenow.androidapp.utils.Resource
import com.example.transactionsapiintegration.di.data.repository.MainRepository
import com.example.transactionsapiintegration.utils.SingleLiveEvent
import com.google.gson.JsonObject
import org.json.JSONObject


class LoginViewModel
    (
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _loginData = SingleLiveEvent<Resource<JsonObject>>()
    val loginData: LiveData<Resource<JsonObject>>
        get() = _loginData

    suspend fun login(
        jsonObject: JsonObject
    ) {
        _loginData.postValue(Resource.loading(null))
        if (networkHelper.isNetworkConnected()) {
            mainRepository.login(jsonObject).let {
                if (it.isSuccessful) {
                    _loginData.postValue(Resource.success(it.body()))
                } else {
                    val errorBodyString = it.errorBody()?.string()
                    // Parse the JSON content to extract the error message
                    val jsonObjectError = JSONObject(errorBodyString!!)
                    val errorMessage = jsonObjectError.getString("message")
                    _loginData.postValue(Resource.error(errorMessage, null))
                }
            }


        } else {
            _loginData.postValue(Resource.error("No internet connection", null))
        }
    }


}