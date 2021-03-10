package com.demo.mmqtnoti

import android.app.Application
import com.demo.mmqtnoti.data.ConfigStore
import com.demo.mmqtnoti.data.ConfigStoreImpl
import com.demo.mmqtnoti.notification.MQTTGateway
import com.demo.mmqtnoti.notification.PushNotificationHandler
import com.demo.mmqtnoti.notification.PushNotificationHandlerImpl
import com.demo.mmqtnoti.notification.PushPayload
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.MqttClient

class PushApplication : Application() {

    lateinit var messageGateway: MQTTGateway
        private set

    private lateinit var configStore: ConfigStore

    private lateinit var pushNotificationHandler: PushNotificationHandler

    private lateinit var gson: Gson

    override fun onCreate() {
        super.onCreate()

        initConfigStore()
        initializeGateway()
        initPushNotificationHandler()
        initGson()
        observePushNotification()

        messageGateway.connect()
    }

    private fun initGson() {
        gson = Gson()
    }

    private fun observePushNotification() {
        messageGateway.setMessageReceivedCallback(object : MQTTGateway.MessageReceivedCallback {
            override fun onReceived(topic: String?, message: String) {
                pushNotificationHandler.handleData(gson.fromJson(message, PushPayload::class.java))
            }
        })
    }

    private fun initPushNotificationHandler() {
        pushNotificationHandler = PushNotificationHandlerImpl(this)
    }

    private fun initConfigStore() {
        configStore = ConfigStoreImpl(this)
    }

    private fun initializeGateway(){
        val clientId = getClientId()
        messageGateway = MQTTGateway(this@PushApplication, clientId)
    }

    private fun getClientId(): String {
        val store = configStore.getClientId()

        if (store.isEmpty()) {
            val newId = MqttClient.generateClientId()
            configStore.setClientId(newId)
        }

        return configStore.getClientId()
    }


}