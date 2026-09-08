package com.bestie.sipkitty

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.bestie.sipkitty.reminder.ReminderReceiver
import com.bestie.sipkitty.reminder.ReminderScheduler
import com.bestie.sipkitty.updater.UpdateWorker

class SipKittyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        UpdateWorker.enqueuePeriodicCheck(this)
        ReminderScheduler.scheduleNext(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Reminder channel
            val reminderChannel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_desc)
                enableVibration(true)
            }

            // Updates channel
            val updateChannel = NotificationChannel(
                "sipkitty_updates",
                "App Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about new SipKitty releases"
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(updateChannel)
        }
    }
}
