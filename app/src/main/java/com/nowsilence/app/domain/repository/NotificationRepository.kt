package com.nowsilence.app.domain.repository

import com.nowsilence.app.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: NotificationEntity): Long
    fun getTodayNotifications(): Flow<List<NotificationEntity>>
    fun getTodayCountByCategory(category: String): Flow<Int>
    suspend fun clearHistory()
}
