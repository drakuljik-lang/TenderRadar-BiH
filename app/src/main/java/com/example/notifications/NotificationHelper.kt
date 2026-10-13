package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.R
import com.example.data.model.NotificationItem

object NotificationHelper {

    const val CHANNEL_ID = "tender_radar_alerts"
    const val CHANNEL_NAME = "TenderRadar obavijesti"
    const val CHANNEL_DESC = "Novi tenderi i upozorenja o rokovima"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESC
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun showSystemNotification(context: Context, item: NotificationItem) {
        ensureChannel(context)

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tenderradar_logo)
            .setContentTitle(item.title)
            .setContentText(item.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(item.id.hashCode(), notif)
        } catch (e: SecurityException) {
            // Permission nije dat — ignoriši
        }
    }
}
