package com.nowsilence.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getNotificationsSince(startOfDay: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE timestamp >= :startOfDay AND category = :category")
    fun countByCategorySince(startOfDay: Long, category: String): Flow<Int>

    @Query("DELETE FROM notifications")
    suspend fun clearHistory()

    @Query("DELETE FROM notifications WHERE timestamp < :beforeMillis")
    suspend fun deleteOlderThan(beforeMillis: Long)
}
