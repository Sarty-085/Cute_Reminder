package com.bestie.sipkitty.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bestie.sipkitty.MainActivity
import com.bestie.sipkitty.R
import java.util.Calendar
import kotlin.random.Random

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "sipkitty_reminders"
        const val NOTIFICATION_ID = 1001
        val VIBRATION_PATTERN = longArrayOf(0, 180, 100, 220) // Gentle double heartbeat vibration

        private val CUTE_MESSAGES = listOf(
            "Purr... Bestie, your kitten says it's time for some water! 🐱💧",
            "Sip check! Take 3 big gulps right now for your glowing skin ✨",
            "Don't be a dry crusty croissant 🥐 Drink water, bestie!",
            "Your kitten is watching... and wants you to stay hydrated! 🐾💖",
            "Water break! Refill your glass and feel energized 🌸",
            "Bestie alert! Hydrate before you diedrate 💧✨",
            "Your friendly kitten reminder to take a fresh sip! 🐱🥤"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.rescheduleOnBoot(context)
            return
        }

        val startHour = intent.getIntExtra(ReminderScheduler.EXTRA_START_HOUR, 9)
        val endHour = intent.getIntExtra(ReminderScheduler.EXTRA_END_HOUR, 21)
        val intervalMinutes = intent.getIntExtra(ReminderScheduler.EXTRA_INTERVAL_MINUTES, 90)

        // Check if current time is within user-configured waking hours
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isQuietHour = if (startHour <= endHour) {
            hour < startHour || hour >= endHour
        } else {
            hour >= endHour && hour < startHour
        }

        if (isQuietHour) {
            // Outside user's designated hours (quiet hours), schedule next check without waking user
            ReminderScheduler.scheduleNext(context, intervalMinutes, startHour, endHour)
            return
        }

        showReminderNotification(context)
        ReminderScheduler.scheduleNext(context, intervalMinutes, startHour, endHour)
    }

    private fun showReminderNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Ensure channel has vibration pattern
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(true)
                vibrationPattern = VIBRATION_PATTERN
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = CUTE_MESSAGES[Random.nextInt(CUTE_MESSAGES.size)]

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SipKitty Reminder 🐱")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(VIBRATION_PATTERN)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
