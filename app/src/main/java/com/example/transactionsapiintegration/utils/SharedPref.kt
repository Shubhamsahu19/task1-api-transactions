package com.example.transactionsapiintegration.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class SharedPref(context: Context) {

    private val pref: SharedPreferences
    private val gson = Gson()

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        pref = EncryptedSharedPreferences.create(
            context,
            "secure_prefs_file",  // Change the name to avoid conflict with unencrypted prefs
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return pref.getString(key, "") ?: ""
    }

    fun saveInt(key: String, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun saveLong(key: String, value: Long) {
        pref.edit().putLong(key, value).apply()
    }

    fun getLong(key: String): Long {
        return pref.getLong(key, 0)
    }

    fun saveFloat(key: String, value: Float) {
        pref.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String): Float {
        return pref.getFloat(key, 0.0f)
    }

    fun saveBoolean(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun clearPreference() {
        pref.edit().clear().apply()
    }
}
