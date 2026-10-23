package com.nowsilence.app.domain.model

enum class NotificationPriority(
    val breaksFilter: Boolean,
    val storedInHistory: Boolean,
    val goesToDailyDigest: Boolean
) {
    CRITICAL(breaksFilter = true, storedInHistory = true, goesToDailyDigest = false),
    NORMAL(breaksFilter = false, storedInHistory = true, goesToDailyDigest = false),
    LOW(breaksFilter = false, storedInHistory = false, goesToDailyDigest = true)
}
