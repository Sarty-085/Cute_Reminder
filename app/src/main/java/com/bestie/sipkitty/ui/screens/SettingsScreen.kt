package com.bestie.sipkitty.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.BuildConfig
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.CreamBackground
import com.bestie.sipkitty.ui.theme.SakuraPink
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.TextSecondary
import com.bestie.sipkitty.ui.theme.WaterBlue
import com.bestie.sipkitty.ui.viewmodel.WaterViewModel

@Composable
fun SettingsScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val prefs by viewModel.userPreferences.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()

    var bestieNameInput by remember(prefs.bestieName) { mutableStateOf(prefs.bestieName) }
    var goalSliderValue by remember(prefs.dailyGoalMl) { mutableFloatStateOf(prefs.dailyGoalMl.toFloat()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Settings & Customization 🌸",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Tailor SipKitty perfectly for your bestie",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 1. Bestie Nickname Card (Saves on Done / Blur, not every keystroke)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Bestie's Name or Nickname 🐱",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bestieNameInput,
                        onValueChange = { bestieNameInput = it },
                        label = { Text("Nickname") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused && bestieNameInput != prefs.bestieName) {
                                    viewModel.updateBestieName(bestieNameInput)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.updateBestieName(bestieNameInput)
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Daily Hydration Goal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Water Goal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${goalSliderValue.toInt()} ml",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = WaterBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Recommended for active daily glow: 2000 - 2500 ml",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = goalSliderValue,
                        onValueChange = { goalSliderValue = it },
                        onValueChangeFinished = {
                            viewModel.updateDailyGoal(goalSliderValue.toInt())
                        },
                        valueRange = 1000f..4000f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = SakuraPink,
                            activeTrackColor = WaterBlue
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Reminder Settings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kitten Water Reminders",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Cute nudges throughout the day",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = prefs.remindersEnabled,
                            onCheckedChange = { viewModel.toggleReminders(it, context) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SakuraPink
                            )
                        )
                    }

                    if (prefs.remindersEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Reminder Frequency",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val intervalOptions = listOf(45, 60, 90, 120)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            intervalOptions.forEach { minutes ->
                                val isSelected = prefs.reminderIntervalMinutes == minutes
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateReminderInterval(minutes, context) },
                                    label = { Text("${minutes}m") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SoftPink,
                                        selectedLabelColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. In-App GitHub Auto-Updater Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "App Updates & Releases 🎀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Installed version: v${BuildConfig.VERSION_NAME}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SipKitty checks GitHub Releases daily for new features. When updated, 100% of your water logs and streak data remain safely intact!",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.checkForUpdates(silent = false) },
                        enabled = !isCheckingUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isCheckingUpdate) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Checking GitHub...", color = Color.White)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Check",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check for Updates Now", color = Color.White)
                        }
                    }

                    if (updateMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = updateMessage ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SakuraPink
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
