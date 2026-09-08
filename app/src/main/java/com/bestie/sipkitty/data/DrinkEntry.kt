package com.bestie.sipkitty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drinks")
data class DrinkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val drinkType: String = "WATER", // WATER, TEA, BOBA, COFFEE, JUICE
    val note: String = ""
)
