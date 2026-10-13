package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Query("SELECT * FROM streak_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<StreakConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: StreakConfig)

    @Query("SELECT * FROM streak_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<StreakLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: StreakLog): Long

    @Update
    suspend fun updateLog(log: StreakLog)

    @Query("DELETE FROM streak_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettings)

    @Query("DELETE FROM streak_logs")
    suspend fun clearAllLogs()

    @Query("DELETE FROM streak_config")
    suspend fun clearConfig()
}
