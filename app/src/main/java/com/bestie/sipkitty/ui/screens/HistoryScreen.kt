package com.bestie.sipkitty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import java.util.Calendar

data class KittyCoatOption(
    val code: String,
    val name: String,
    val description: String,
    val swatchColor: Color,
    val emoji: String
)

data class QuestBadge(
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val progressLabel: String,
    val iconEmoji: String,
    val accessoryCode: String,
    val accessoryName: String,
    val backgroundColor: Color
)

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
    val todayDrinks by viewModel.todayDrinks.collectAsState()
    val todayTotal by viewModel.todayTotalMl.collectAsState()
    val lifetimeDrinks by viewModel.lifetimeDrinksCount.collectAsState()
    val maxDailyIntake by viewModel.maxDailyIntake.collectAsState()

    // Coat options
    val coats = listOf(
        KittyCoatOption("WHITE", "Snowball", "Pure white fur & pink ears", Color(0xFFFFFDF9), "🤍"),
        KittyCoatOption("ORANGE", "Marmalade", "Warm ginger tabby with stripes", Color(0xFFFFB049), "🍊"),
        KittyCoatOption("BLACK", "Midnight", "Sleek black cat & golden eyes", Color(0xFF2C2C34), "🖤"),
        KittyCoatOption("CALICO", "Calico", "Tri-color ginger & brown cutie", Color(0xFFFFCC80), "🤎"),
        KittyCoatOption("PINK", "Sakura", "Pastel blush pink fantasy kitty", Color(0xFFFFD1DC), "🌸")
    )

    // Evaluate Quests
    val hasEarlyBird = todayDrinks.any {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        cal.get(Calendar.HOUR_OF_DAY) < 9
    }
    val hasNightOwl = todayDrinks.any {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        cal.get(Calendar.HOUR_OF_DAY) >= 19
    }
    val hasTripleSplash = todayDrinks.size >= 3
    val highestDaily = maxOf(todayTotal, maxDailyIntake)
    val isHydrationHero = highestDaily >= 2500
    val isCenturyClub = lifetimeDrinks >= 100
    val is10DayStreak = streak >= 10

    val quests = listOf(
        QuestBadge(
            title = "Early Bird Sip",
            description = "Logged a refreshing drink before 9:00 AM",
            isCompleted = hasEarlyBird,
            progressLabel = if (hasEarlyBird) "Completed today!" else "Drink before 9 AM",
            iconEmoji = "🌅",
            accessoryCode = "BELL",
            accessoryName = "🔔 Golden Bell Collar",
            backgroundColor = GoldStar.copy(alpha = 0.35f)
        ),
        QuestBadge(
            title = "Night Owl Sip",
            description = "Stayed nicely hydrated in the evening (after 7 PM)",
            isCompleted = hasNightOwl,
            progressLabel = if (hasNightOwl) "Completed today!" else "Drink after 7 PM",
            iconEmoji = "🦉",
            accessoryCode = "STAR",
            accessoryName = "⭐ Sleepy Star Clip",
            backgroundColor = LavenderPastel
        ),
        QuestBadge(
            title = "Triple Splash",
            description = "Logged at least 3 distinct drinks in one day",
            isCompleted = hasTripleSplash,
            progressLabel = "${minOf(3, todayDrinks.size)} / 3 drinks today",
            iconEmoji = "⚡",
            accessoryCode = "WIZARD",
            accessoryName = "🧙 Magic Wizard Hat",
            backgroundColor = MintPastel
        ),
        QuestBadge(
            title = "Hydration Hero",
            description = "Crushed 2,500ml or more in a single day",
            isCompleted = isHydrationHero,
            progressLabel = "$highestDaily / 2,500 ml max",
            iconEmoji = "🌊",
            accessoryCode = "CROWN",
            accessoryName = "👑 Ocean Crystal Crown",
            backgroundColor = WaterBlue.copy(alpha = 0.25f)
        ),
        QuestBadge(
            title = "Century Club",
            description = "Logged a total of 100 lifetime glasses/drinks",
            isCompleted = isCenturyClub,
            progressLabel = "$lifetimeDrinks / 100 lifetime drinks",
            iconEmoji = "🏅",
            accessoryCode = "WINGS",
            accessoryName = "🪽 Angel Wings",
            backgroundColor = SoftPink.copy(alpha = 0.6f)
        ),
        QuestBadge(
            title = "Master Baker",
            description = "Reached a double-digit 10-day hydration streak",
            isCompleted = is10DayStreak,
            progressLabel = "$streak / 10 days streak",
            iconEmoji = "👨‍🍳",
            accessoryCode = "CHEF",
            accessoryName = "👨‍🍳 Cute Baker Hat",
            backgroundColor = PeachPastel
        )
    )

    // Streak Badges
    val streakBadges = listOf(
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
                text = "Bestie's Journey & Salon 🎀",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Customize kitty breeds and unlock magical accessories!",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Stats Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$streak Days",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(text = "Streak", fontSize = 11.sp, color = TextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎯", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${prefs.dailyGoalMl} ml",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue
                        )
                        Text(text = "Daily Goal", fontSize = 11.sp, color = TextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🥤", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$lifetimeDrinks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(text = "Total Drinks", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        // Section 1: Kitty Breed & Coat Salon
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Kitty Coat Salon 🐾",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Pick the breed and fur color for your mascot",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    coats.forEach { coat ->
                        val isSelected = prefs.kittyCoat == coat.code

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SoftPink.copy(alpha = 0.5f) else CardSurface
                            ),
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) SakuraPink else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipCoat(coat.code)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(coat.swatchColor)
                                        .border(1.5.dp, Color(0xFF6D4C41).copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = coat.emoji, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = coat.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isSelected) "Equipped ✨" else "Select",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) SakuraPink else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section 2: Hydration Quests & Achievements
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hydration Quests 🏆",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Daily and milestone achievements to conquer",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(quests.size) { index ->
            val quest = quests[index]
            val isEquipped = prefs.equippedAccessory == quest.accessoryCode

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEquipped) SoftPink.copy(alpha = 0.35f) else CardSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (quest.isCompleted) quest.backgroundColor else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (quest.isCompleted) {
                            Text(text = quest.iconEmoji, fontSize = 24.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = quest.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (quest.isCompleted) TextPrimary else TextSecondary
                            )
                            if (quest.isCompleted) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Completed",
                                    tint = GoldStar,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = quest.description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlocks: ${quest.accessoryName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (quest.isCompleted) WaterBlue else TextSecondary.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (quest.isCompleted) {
                        if (isEquipped) {
                            Button(
                                onClick = {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipAccessory("NONE")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Worn", modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Worn", fontSize = 11.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipAccessory(quest.accessoryCode)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Wear", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    } else {
                        Text(
                            text = quest.progressLabel,
                            fontSize = 10.sp,
                            color = SoftPink,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section 3: Streak Milestones & Wardrobe
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Streak Milestones 🔥",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Consistency rewards for maintaining hydration streaks",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (prefs.equippedAccessory != "NONE") {
                    OutlinedButton(
                        onClick = {
                            SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                            viewModel.equipAccessory("NONE")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Unequip", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(streakBadges.size) { index ->
            val badge = streakBadges[index]
            val isUnlocked = streak >= badge.requiredStreak
            val isEquipped = prefs.equippedAccessory == badge.accessoryCode
            val daysLeft = maxOf(0, badge.requiredStreak - streak)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEquipped) SoftPink.copy(alpha = 0.35f) else CardSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) badge.backgroundColor else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = badge.iconEmoji, fontSize = 24.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isUnlocked) TextPrimary else TextSecondary
                            )
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Unlocked",
                                    tint = GoldStar,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badge.description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlocks: ${badge.accessoryName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUnlocked) WaterBlue else TextSecondary.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isUnlocked) {
                        if (isEquipped) {
                            Button(
                                onClick = {
                                    SoundEffectManager.playBubblePop(context, prefs.soundEnabled)
                                    viewModel.equipAccessory("NONE")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Worn", modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
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
