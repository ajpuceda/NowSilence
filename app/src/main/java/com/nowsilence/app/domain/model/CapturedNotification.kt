package com.nowsilence.app.domain.model

data class CapturedNotification(
    val packageName: String,
    val title: String,
    val text: String,
    val postedAtMillis: Long,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)
