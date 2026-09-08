package com.bestie.sipkitty.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bestie.sipkitty.data.AppDatabase
import com.bestie.sipkitty.data.DrinkEntry
import com.bestie.sipkitty.data.UserPreferences
import com.bestie.sipkitty.data.UserPreferencesRepository
import com.bestie.sipkitty.reminder.ReminderScheduler
import com.bestie.sipkitty.updater.ApkInstaller
import com.bestie.sipkitty.updater.GitHubReleaseChecker
import com.bestie.sipkitty.updater.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val drinkDao = db.drinkDao()
    private val preferencesRepository = UserPreferencesRepository(application)
    private val releaseChecker = GitHubReleaseChecker(application)

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(
                dailyGoalMl = 2000,
                remindersEnabled = true,
                reminderIntervalMinutes = 90,
                startHour = 9,
                endHour = 21,
                bestieName = "Bestie",
                lastUpdateCheckTime = 0L
            )
        )

    private val _todayTotalMl = MutableStateFlow(0)
    val todayTotalMl: StateFlow<Int> = _todayTotalMl.asStateFlow()

    private val _todayDrinks = MutableStateFlow<List<DrinkEntry>>(emptyList())
    val todayDrinks: StateFlow<List<DrinkEntry>> = _todayDrinks.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    // Update state
    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _isDownloadingUpdate = MutableStateFlow(false)
    val isDownloadingUpdate: StateFlow<Boolean> = _isDownloadingUpdate.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage.asStateFlow()

    init {
        loadTodayData()
        calculateStreak()
    }

    private fun getDayStartAndEndTime(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = cal.timeInMillis

        cal.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = cal.timeInMillis

        return Pair(startTime, endTime)
    }

    private fun loadTodayData() {
        val (startTime, endTime) = getDayStartAndEndTime()

        viewModelScope.launch {
            drinkDao.getTotalBetween(startTime, endTime).collect { total ->
                _todayTotalMl.value = total
            }
        }

        viewModelScope.launch {
            drinkDao.getDrinksBetween(startTime, endTime).collect { drinks ->
                _todayDrinks.value = drinks
            }
        }
    }

    private fun calculateStreak() {
        viewModelScope.launch {
            drinkDao.getDistinctIntakeDays().collect { days ->
                if (days.isEmpty()) {
                    _currentStreak.value = 0
                    return@collect
                }

                val currentDayNumber = System.currentTimeMillis() / 86400000L
                var streak = 0
                var expectedDay = if (days.contains(currentDayNumber)) currentDayNumber else currentDayNumber - 1

                for (day in days) {
                    if (day == expectedDay) {
                        streak++
                        expectedDay--
                    } else if (day < expectedDay) {
                        break
                    }
                }
                _currentStreak.value = streak
            }
        }
    }

    fun addDrink(amountMl: Int, drinkType: String = "WATER", note: String = "") {
        viewModelScope.launch {
            drinkDao.insertDrink(
                DrinkEntry(
                    amountMl = amountMl,
                    drinkType = drinkType,
                    note = note
                )
            )
            calculateStreak()
        }
    }

    fun deleteDrink(drink: DrinkEntry) {
        viewModelScope.launch {
            drinkDao.deleteDrink(drink)
            calculateStreak()
        }
    }

    fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDailyGoal(goalMl)
        }
    }

    fun updateBestieName(name: String) {
        viewModelScope.launch {
            preferencesRepository.updateBestieName(name)
        }
    }

    fun toggleReminders(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            preferencesRepository.updateRemindersEnabled(enabled)
            if (enabled) {
                ReminderScheduler.scheduleNext(context, userPreferences.value.reminderIntervalMinutes)
            } else {
                ReminderScheduler.cancel(context)
            }
        }
    }

    fun updateReminderInterval(minutes: Int, context: Context) {
        viewModelScope.launch {
            preferencesRepository.updateReminderInterval(minutes)
            if (userPreferences.value.remindersEnabled) {
                ReminderScheduler.scheduleNext(context, minutes)
            }
        }
    }

    fun checkForUpdates(silent: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            _updateMessage.value = null

            val result = releaseChecker.checkForUpdates()
            _isCheckingUpdate.value = false

            result.onSuccess { info ->
                preferencesRepository.updateLastUpdateCheckTime(System.currentTimeMillis())
                if (info.isUpdateAvailable) {
                    _updateInfo.value = info
                } else if (!silent) {
                    _updateMessage.value = "SipKitty is already on the latest version! 🌸"
                }
            }.onFailure { err ->
                if (!silent) {
                    _updateMessage.value = "Could not check for updates: ${err.localizedMessage ?: "Unknown error"}"
                }
            }
        }
    }

    fun downloadAndInstallUpdate(context: Context) {
        val info = _updateInfo.value ?: return

        if (!ApkInstaller.canInstallApks(context)) {
            ApkInstaller.openInstallPermissionSettings(context)
            return
        }

        viewModelScope.launch {
            _isDownloadingUpdate.value = true
            _downloadProgress.value = 0

            val result = ApkInstaller.downloadApk(
                context = context,
                downloadUrl = info.downloadUrl,
                fileName = info.fileName
            ) { progress ->
                _downloadProgress.value = progress
            }

            _isDownloadingUpdate.value = false

            result.onSuccess { apkFile ->
                _updateInfo.value = null
                ApkInstaller.installApk(context, apkFile)
            }.onFailure { err ->
                _updateMessage.value = "Download failed: ${err.localizedMessage}"
            }
        }
    }

    fun dismissUpdateDialog() {
        _updateInfo.value = null
    }

    fun clearUpdateMessage() {
        _updateMessage.value = null
    }
}
