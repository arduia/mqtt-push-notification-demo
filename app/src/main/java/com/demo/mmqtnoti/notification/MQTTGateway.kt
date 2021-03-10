package com.demo.mmqtnoti.notification

import android.content.Context
import android.util.Log
import com.demo.mmqtnoti.BuildConfig
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.lang.Exception

class MQTTGateway(context: Context, clientId: String) {

    private val _connectionStatus = ConflatedBroadcastChannel<ConnectionStatus>()

    val connectionStatus: Flow<ConnectionStatus> = _connectionStatus.asFlow()

    private val client = MqttAndroidClient(context, BuildConfig.PUSH_NOTI_HOST_TCP, clientId)
    private var messageReceivedCallback: MessageReceivedCallback? = null

    init {
        _connectionStatus.sendBlocking(ConnectionStatus.Disconnected) //Initial as Disconnected!
        observeConnectionStatus()
    }

    fun connect() {
        Log.d("MQTTGateway", "connect")
        _connectionStatus.sendBlocking(ConnectionStatus.Connecting)
        val connectOption = getConnectOption()
        try {
            client.connect(connectOption, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    _connectionStatus.sendBlocking(ConnectionStatus.Connected)
                    subscribeDesireTopic()
                    Log.d("MQTT Gateway", "onSuccess Connected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    _connectionStatus.sendBlocking(ConnectionStatus.Disconnected)
                    Log.d("MQTT Gateway", "onFailure $exception")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            _connectionStatus.sendBlocking(ConnectionStatus.Disconnected)
        }

    }

    fun disconnect() {
        if (client.isConnected.not()) return
        client.disconnect()
    }

    private fun subscribeDesireTopic() {
        val topics = PushNotificationConfig.LISTEN_TOPICS.keys
        val qos = PushNotificationConfig.LISTEN_TOPICS.values
        client.subscribe(topics.toTypedArray(), qos.toIntArray())
    }

    private fun observeConnectionStatus() {
        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                _connectionStatus.sendBlocking(ConnectionStatus.Disconnected)
                Log.d("MQTTGateWay", "Disconnected")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                messageReceivedCallback?.onReceived(topic, message.toString())
                Log.d("MQTTGateway", "message Received : topic: $topic, message: $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
    }

    private fun getConnectOption() = MqttConnectOptions().apply {
        userName = BuildConfig.PUSH_NOTI_USERNAME
        password = BuildConfig.PUSH_NOTI_PASSWORD.toCharArray()
        isCleanSession = IS_CLEAN_SESSION
        isAutomaticReconnect = IS_AUTOMATIC_RECONNECT
    }

    fun setMessageReceivedCallback(callback: MessageReceivedCallback?) {
        this.messageReceivedCallback = callback
    }

    interface MessageReceivedCallback {
        fun onReceived(topic: String?, message: String)
    }

    companion object {
        private const val IS_CLEAN_SESSION = false
        private const val IS_AUTOMATIC_RECONNECT = true
    }
}