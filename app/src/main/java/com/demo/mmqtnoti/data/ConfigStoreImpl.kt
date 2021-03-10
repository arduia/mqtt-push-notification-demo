package com.demo.mmqtnoti.data

import android.content.Context

class ConfigStoreImpl(private val context: Context) : ConfigStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun setClientId(id: String) {
        preferences.edit()
            .putString(KEY_CLIENT_ID, id)
            .apply()
    }

    override fun getClientId(): String {
        return preferences.getString(KEY_CLIENT_ID, DEFAULT_CLIENT_ID) ?: DEFAULT_CLIENT_ID
    }

    companion object {
        private const val FILE_NAME = "config_store.xml"
        private const val KEY_CLIENT_ID = "key_client_id"
        private const val DEFAULT_CLIENT_ID = ""
    }

}