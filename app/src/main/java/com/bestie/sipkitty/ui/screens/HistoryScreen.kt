package com.bestie.sipkitty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.CreamBackground
import com.bestie.sipkitty.ui.theme.GoldStar
import com.bestie.sipkitty.ui.theme.LavenderPastel
import com.bestie.sipkitty.ui.theme.MintPastel
import com.bestie.sipkitty.ui.theme.PeachPastel
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.TextSecondary
import com.bestie.sipkitty.ui.theme.WaterBlue
import com.bestie.sipkitty.ui.viewmodel.WaterViewModel

data class StreakBadge(
    val title: String,
    val description: String,
    val requiredStreak: Int,
    val iconEmoji: String,
    val backgroundColor: Color
)

@Composable
fun HistoryScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val streak by viewModel.currentStreak.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    val badges = listOf(
        StreakBadge("First Drop", "Logged your first hydration day", 1, "💧", SoftPink),
        StreakBadge("Hydrated Kitten", "Maintained a 3-day water streak", 3, "🐱", MintPastel),
        StreakBadge("Glow Queen", "7 days of glowing healthy skin", 7, "✨", LavenderPastel),
        StreakBadge("Water Master", "14 continuous days of hydration", 14, "👑", PeachPastel),
        StreakBadge("Eternal Purr", "A full 30 days of top hydration", 30, "💖", GoldStar.copy(alpha = 0.6f))
    )

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
                text = "Bestie's Hydration Journey 🎀",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Celebrate your streaks & collect cute stickers!",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Streak Card Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$streak Days",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Current Streak",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎯", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${prefs.dailyGoalMl} ml",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue
                        )
                        Text(
                            text = "Daily Target",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sticker Scrapbook Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kitten Sticker Scrapbook 🐾",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Badges list
        items(badges.size) { index ->
            val badge = badges[index]
            val isUnlocked = streak >= badge.requiredStreak
            val daysLeft = maxOf(0, badge.requiredStreak - streak)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) CardSurface else CardSurface.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) badge.backgroundColor else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = badge.iconEmoji, fontSize = 28.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUnlocked) TextPrimary else TextSecondary
                            )
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Unlocked",
                                    tint = GoldStar,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badge.description,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUnlocked) "Unlocked! 🎉" else "Need ${badge.requiredStreak} day streak ($daysLeft days left)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isUnlocked) Color(0xFF2E7D32) else SoftPink
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
