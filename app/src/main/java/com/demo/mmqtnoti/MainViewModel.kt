package com.demo.mmqtnoti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.mmqtnoti.notification.ConnectionStatus
import com.demo.mmqtnoti.notification.MQTTGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(private val messageGateway: MQTTGateway): ViewModel() {

    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> get() = _connectionStatus

    init {
        _connectionStatus.value = ConnectionStatus.Disconnected
        observeConnectionStatus()
    }

    private fun observeConnectionStatus(){
        messageGateway.connectionStatus
            .flowOn(Dispatchers.IO)
            .onEach {
                _connectionStatus.postValue(it)
            }
            .launchIn(viewModelScope)
    }

    fun connect(){
        messageGateway.connect()
    }

    fun disconnect(){
        messageGateway.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
    }
}