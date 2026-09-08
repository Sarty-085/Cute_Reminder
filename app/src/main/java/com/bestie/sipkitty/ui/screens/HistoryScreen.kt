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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.sound.SoundEffectManager
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.CreamBackground
import com.bestie.sipkitty.ui.theme.GoldStar
import com.bestie.sipkitty.ui.theme.LavenderPastel
import com.bestie.sipkitty.ui.theme.MintPastel
import com.bestie.sipkitty.ui.theme.PeachPastel
import com.bestie.sipkitty.ui.theme.SakuraPink
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
    val accessoryCode: String,
    val accessoryName: String,
    val backgroundColor: Color
)

@Composable
fun HistoryScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val streak by viewModel.currentStreak.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    val badges = listOf(
        StreakBadge("First Drop", "Logged your first hydration day", 1, "💧", "FLOWER", "🌸 Flower Clip", SoftPink),
        StreakBadge("Hydrated Kitten", "Maintained a 3-day water streak", 3, "🐱", "RIBBON", "🎀 Silk Bow", MintPastel),
        StreakBadge("Glow Queen", "7 days of glowing healthy skin", 7, "✨", "BERET", "🍓 Strawberry Beret", LavenderPastel),
        StreakBadge("Water Master", "14 continuous days of hydration", 14, "🕶️", "SUNGLASSES", "🕶️ Cool Shades", PeachPastel),
        StreakBadge("Boba Champion", "21 continuous days of hydration", 21, "🧋", "BOBA", "🧋 Handheld Boba", SoftPink.copy(alpha = 0.8f)),
        StreakBadge("Eternal Purr", "A full 30 days of top hydration", 30, "👑", "TIARA", "👑 Princess Tiara", GoldStar.copy(alpha = 0.6f))
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
                text = "Streaks unlock wearable accessories for your kitten!",
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

        // Kitten Closet Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kitten Closet & Wardrobe 🐾",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (prefs.equippedAccessory != "NONE") {
                    OutlinedButton(
                        onClick = {
                            SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                            viewModel.equipAccessory("NONE")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Unequip All", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Badges & Wardrobe list
        items(badges.size) { index ->
            val badge = badges[index]
            val isUnlocked = streak >= badge.requiredStreak
            val isEquipped = prefs.equippedAccessory == badge.accessoryCode
            val daysLeft = maxOf(0, badge.requiredStreak - streak)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEquipped) SoftPink.copy(alpha = 0.35f) else CardSurface
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
                            text = "Unlocks: ${badge.accessoryName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUnlocked) WaterBlue else TextSecondary.copy(alpha = 0.7f)
                        )
                    }

                    // Equip / Unequip Action Button
                    if (isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isEquipped) {
                            Button(
                                onClick = {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipAccessory("NONE")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Equipped", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Worn", fontSize = 11.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipAccessory(badge.accessoryCode)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Wear", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    } else {
                        Text(
                            text = "$daysLeft d left",
                            fontSize = 11.sp,
                            color = SoftPink,
                            fontWeight = FontWeight.Medium
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
