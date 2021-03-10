package com.demo.mmqtnoti.data

interface ConfigStore {

    fun setClientId(id: String)

    fun getClientId(): String

}