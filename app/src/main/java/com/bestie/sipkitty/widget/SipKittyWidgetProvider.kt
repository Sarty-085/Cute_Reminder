package com.bestie.sipkitty.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.RemoteViews
import android.widget.Toast
import com.bestie.sipkitty.MainActivity
import com.bestie.sipkitty.R
import com.bestie.sipkitty.data.AppDatabase
import com.bestie.sipkitty.data.DrinkEntry
import com.bestie.sipkitty.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class SipKittyWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_QUICK_ADD_WATER = "com.bestie.sipkitty.ACTION_QUICK_ADD_WATER"
        const val ACTION_DATA_CHANGED = "com.bestie.sipkitty.ACTION_DATA_CHANGED"

        fun notifyDataChanged(context: Context) {
            val intent = Intent(context, SipKittyWidgetProvider::class.java).apply {
                action = ACTION_DATA_CHANGED
            }
            context.sendBroadcast(intent)
        }
    }

    private val widgetScope = CoroutineScope(Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_QUICK_ADD_WATER -> {
                val pendingResult = goAsync()
                widgetScope.launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        db.drinkDao().insertDrink(
                            DrinkEntry(
                                amountMl = 250,
                                drinkType = "WATER"
                            )
                        )

                        // Trigger soft haptic
                        try {
                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator?.vibrate(40)
                            }
                        } catch (_: Exception) {
                        }

                        // Update all widget instances
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        val component = ComponentName(context, SipKittyWidgetProvider::class.java)
                        val appWidgetIds = appWidgetManager.getAppWidgetIds(component)
                        for (id in appWidgetIds) {
                            updateWidget(context, appWidgetManager, id)
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Purr! +250ml logged 🐱💧", Toast.LENGTH_SHORT).show()
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ACTION_DATA_CHANGED -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val component = ComponentName(context, SipKittyWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(component)
                for (id in appWidgetIds) {
                    updateWidget(context, appWidgetManager, id)
                }
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        widgetScope.launch {
            val db = AppDatabase.getInstance(context)
            val prefsRepo = UserPreferencesRepository(context)
            val prefs = prefsRepo.userPreferencesFlow.first()

            // Calculate start and end of today
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            cal.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endOfDay = cal.timeInMillis

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val isSleepMode = if (prefs.startHour <= prefs.endHour) {
                currentHour < prefs.startHour || currentHour >= prefs.endHour
            } else {
                currentHour >= prefs.endHour && currentHour < prefs.startHour
            }

            val totalMl = db.drinkDao().getTotalBetween(startOfDay, endOfDay).first()
            val goalMl = if (prefs.dailyGoalMl > 0) prefs.dailyGoalMl else 2000
            val percent = ((totalMl.toFloat() / goalMl) * 100).toInt().coerceIn(0, 100)

            val views = RemoteViews(context.packageName, R.layout.widget_sipkitty)

            // Click to open app
            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // Click to quick add water (+250ml)
            val addIntent = Intent(context, SipKittyWidgetProvider::class.java).apply {
                action = ACTION_QUICK_ADD_WATER
            }
            val addPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_add, addPendingIntent)

            // Update texts and progress
            views.setTextViewText(R.id.widget_title, "SipKitty • ${prefs.bestieName} 🌸")
            views.setTextViewText(R.id.widget_progress_text, "$totalMl / $goalMl ml")
            views.setTextViewText(R.id.widget_percent_text, "$percent%")
            views.setProgressBar(R.id.widget_progress_bar, 100, percent, false)

            val statusText = when {
                isSleepMode -> "Kitty is sleeping... 🌙 Rest well!"
                percent >= 100 -> "Goal crushed! You're glowing! 👑✨"
                percent >= 40 -> "Purr! Great hydration rhythm! 🐾"
                else -> "Take a sip bestie! Kitty is thirsty 🥺"
            }
            views.setTextViewText(R.id.widget_status, statusText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
