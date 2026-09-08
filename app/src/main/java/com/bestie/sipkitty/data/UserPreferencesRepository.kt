package com.bestie.sipkitty.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sipkitty_preferences")

data class UserPreferences(
    val dailyGoalMl: Int,
    val remindersEnabled: Boolean,
    val reminderIntervalMinutes: Int,
    val startHour: Int,
    val endHour: Int,
    val bestieName: String,
    val lastUpdateCheckTime: Long
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val DAILY_GOAL = intPreferencesKey("daily_goal_ml")
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val REMINDER_INTERVAL = intPreferencesKey("reminder_interval_minutes")
        val START_HOUR = intPreferencesKey("start_hour")
        val END_HOUR = intPreferencesKey("end_hour")
        val BESTIE_NAME = stringPreferencesKey("bestie_name")
        val LAST_UPDATE_CHECK = longPreferencesKey("last_update_check")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            dailyGoalMl = preferences[PreferencesKeys.DAILY_GOAL] ?: 2000,
            remindersEnabled = preferences[PreferencesKeys.REMINDERS_ENABLED] ?: true,
            reminderIntervalMinutes = preferences[PreferencesKeys.REMINDER_INTERVAL] ?: 90,
            startHour = preferences[PreferencesKeys.START_HOUR] ?: 9,
            endHour = preferences[PreferencesKeys.END_HOUR] ?: 21,
            bestieName = preferences[PreferencesKeys.BESTIE_NAME] ?: "Bestie",
            lastUpdateCheckTime = preferences[PreferencesKeys.LAST_UPDATE_CHECK] ?: 0L
        )
    }

    suspend fun updateDailyGoal(goalMl: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_GOAL] = goalMl
        }
    }

    suspend fun updateRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun updateReminderInterval(intervalMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_INTERVAL] = intervalMinutes
        }
    }

    suspend fun updateQuietHours(startHour: Int, endHour: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_HOUR] = startHour
            preferences[PreferencesKeys.END_HOUR] = endHour
        }
    }

    suspend fun updateBestieName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BESTIE_NAME] = name
        }
    }

    suspend fun updateLastUpdateCheckTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_UPDATE_CHECK] = timestamp
        }
    }
}
