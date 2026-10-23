package com.nowsilence.app.data.repository

import com.nowsilence.app.data.local.NotificationDao
import com.nowsilence.app.data.local.NotificationEntity
import com.nowsilence.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class NotificationRepositoryImpl(
    private val dao: NotificationDao
) : NotificationRepository {

    override suspend fun saveNotification(notification: NotificationEntity): Long =
        dao.insert(notification)

    override fun getTodayNotifications(): Flow<List<NotificationEntity>> =
        dao.getNotificationsSince(startOfDay())

    override fun getTodayCountByCategory(category: String): Flow<Int> =
        dao.countByCategorySince(startOfDay(), category)

    override suspend fun clearHistory() = dao.clearHistory()

    private fun startOfDay(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
