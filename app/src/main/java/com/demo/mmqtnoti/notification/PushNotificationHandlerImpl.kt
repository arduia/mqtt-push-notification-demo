package com.demo.mmqtnoti.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.demo.mmqtnoti.MainActivity
import kotlin.random.Random

class PushNotificationHandlerImpl(
    private val context: Context
) : PushNotificationHandler {

    private val notificationManager =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID_NEW,
            CHANNEL_NAME_NEW,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun handleData(payload: PushPayload) {

        val pendingIntent = getPendingIntentForMainActivity()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_NEW)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(payload.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(payload.content)
            .setVibrate(longArrayOf(30, 20))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationId = when (payload.type) {
            "two" -> 2
            "three" -> 3
            else -> NOTIFICATION_ID_NEW
        }
        notificationManager.notify(notificationId, builder.build())
    }

    private fun getPendingIntentForMainActivity(): PendingIntent {
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val CHANNEL_ID_NEW = "notification_new_channel"
        private const val CHANNEL_NAME_NEW = "News"
        private const val NOTIFICATION_ID_NEW = 1
    }

}