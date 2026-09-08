package com.bestie.sipkitty.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bestie.sipkitty.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ReminderScheduler {

    private const val REQUEST_CODE = 2002
    const val EXTRA_START_HOUR = "EXTRA_START_HOUR"
    const val EXTRA_END_HOUR = "EXTRA_END_HOUR"
    const val EXTRA_INTERVAL_MINUTES = "EXTRA_INTERVAL_MINUTES"

    fun scheduleNext(
        context: Context,
        intervalMinutes: Int = 90,
        startHour: Int = 9,
        endHour: Int = 21
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.bestie.sipkitty.ACTION_WATER_REMINDER"
            putExtra(EXTRA_START_HOUR, startHour)
            putExtra(EXTRA_END_HOUR, endHour)
            putExtra(EXTRA_INTERVAL_MINUTES, intervalMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (e: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun rescheduleOnBoot(context: Context) {
        try {
            val repository = UserPreferencesRepository(context)
            val prefs = runBlocking { repository.userPreferencesFlow.first() }
            if (prefs.remindersEnabled) {
                scheduleNext(
                    context = context,
                    intervalMinutes = prefs.reminderIntervalMinutes,
                    startHour = prefs.startHour,
                    endHour = prefs.endHour
                )
            }
        } catch (e: Exception) {
            // Fallback to safe defaults
            scheduleNext(context, 90, 9, 21)
        }
    }
}
