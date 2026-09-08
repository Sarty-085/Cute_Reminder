package com.bestie.sipkitty.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.LavenderPastel
import com.bestie.sipkitty.ui.theme.MintPastel
import com.bestie.sipkitty.ui.theme.PeachPastel
import com.bestie.sipkitty.ui.theme.SakuraPink
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.WaterBlue
import com.bestie.sipkitty.ui.theme.WaterBlueLight

data class QuickDrink(
    val emoji: String,
    val label: String,
    val amountMl: Int,
    val drinkType: String,
    val backgroundColor: Color
)

@Composable
fun QuickAddButtons(
    onAddDrink: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }

    val quickOptions = listOf(
        QuickDrink("🥛", "Glass", 250, "WATER", WaterBlueLight),
        QuickDrink("🍼", "Bottle", 500, "WATER", MintPastel.copy(alpha = 0.6f)),
        QuickDrink("☕", "Cup", 150, "TEA", PeachPastel.copy(alpha = 0.6f)),
        QuickDrink("🧋", "Boba", 350, "BOBA", LavenderPastel.copy(alpha = 0.6f))
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Sip 💖",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            quickOptions.forEach { drink ->
                Card(
                    onClick = { onAddDrink(drink.amountMl, drink.drinkType) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = drink.backgroundColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = drink.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${drink.amountMl}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "ml",
                            fontSize = 11.sp,
                            color = TextPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Custom + Button
            Card(
                onClick = { showCustomDialog = true },
                modifier = Modifier.weight(0.9f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Custom Amount",
                        tint = SakuraPink,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Custom",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "amount",
                        fontSize = 10.sp,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomAmountDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { amount, type ->
                onAddDrink(amount, type)
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun CustomAmountDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(300f) }
    var selectedDrinkType by remember { mutableStateOf("WATER") }

    val drinkTypes = listOf(
        "WATER" to "🥛 Water",
        "TEA" to "☕ Tea",
        "BOBA" to "🧋 Boba",
        "COFFEE" to "☕ Coffee",
        "JUICE" to "🥤 Juice"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Custom Drink 🐱💧",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${sliderValue.toInt()} ml",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = WaterBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 50f..1200f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = SakuraPink,
                        activeTrackColor = WaterBlue
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Select Drink Type:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    drinkTypes.take(3).forEach { (type, label) ->
                        FilterChip(
                            selected = selectedDrinkType == type,
                            onClick = { selectedDrinkType = type },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftPink,
                                selectedLabelColor = TextPrimary
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    drinkTypes.drop(3).forEach { (type, label) ->
                        FilterChip(
                            selected = selectedDrinkType == type,
                            onClick = { selectedDrinkType = type },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftPink,
                                selectedLabelColor = TextPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(sliderValue.toInt(), selectedDrinkType) },
                colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
            ) {
                Text("Log Sip", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextPrimary)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(22.dp)
    )
}
