package com.demo.mmqtnoti.notification

interface PushNotificationHandler {
    fun handleData(payload: PushPayload)
}