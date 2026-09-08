package com.bestie.sipkitty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: DrinkEntry): Long

    @Delete
    suspend fun deleteDrink(drink: DrinkEntry)

    @Query("SELECT * FROM drinks WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getDrinksBetween(startTime: Long, endTime: Long): Flow<List<DrinkEntry>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drinks WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getTotalBetween(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT * FROM drinks ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentDrinks(limit: Int = 20): Flow<List<DrinkEntry>>

    @Query("SELECT DISTINCT (timestamp / 86400000) FROM drinks ORDER BY (timestamp / 86400000) DESC")
    fun getDistinctIntakeDays(): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM drinks")
    suspend fun getTotalDrinkCount(): Int

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drinks")
    suspend fun getLifetimeTotal(): Long
}
