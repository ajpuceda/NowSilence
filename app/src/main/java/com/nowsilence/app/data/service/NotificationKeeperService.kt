package com.nowsilence.app.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nowsilence.app.data.ai.LocalClassifierAI
import com.nowsilence.app.data.local.NotificationEntity
import com.nowsilence.app.data.local.NowSilenceDatabase
import com.nowsilence.app.data.repository.NotificationRepositoryImpl
import com.nowsilence.app.domain.model.NotificationPriority
import com.nowsilence.app.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Listener de notificaciones de NowSilence.
 *
 * Intercepta cada notificación entrante, la clasifica con la IA local,
 * silencia las LOW y persiste NORMAL/LOW en Room (historial + resumen).
 */
class NotificationKeeperService : NotificationListenerService() {

    private val classifier by lazy { LocalClassifierAI.getInstance(this) }
    private val repository: NotificationRepository by lazy {
        NotificationRepositoryImpl(
            NowSilenceDatabase.getInstance(this).notificationDao()
        )
    }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        classifier.initialize()
        Log.d(TAG, "NotificationKeeperService creado")
    }

    override fun onDestroy() {
        classifier.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || !shouldProcess(sbn)) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val text = extras.getCharSequence("android.text")?.toString().orEmpty()
        val packageName = sbn.packageName ?: "unknown"

        val priority = classifier.classifyNotification(title, text)

        Log.d(TAG, "Notificación [$packageName] prioridad=$priority título=$title")

        if (priority == NotificationPriority.NORMAL || priority == NotificationPriority.LOW) {
            serviceScope.launch {
                repository.saveNotification(
                    NotificationEntity(
                        packageName = packageName,
                        title = title,
                        message = text,
                        timestamp = sbn.postTime,
                        category = priority.name
                    )
                )
            }
        }

        if (priority == NotificationPriority.LOW && isFilterEnabled()) {
            silenceNotification(sbn)
        }
        // CRITICAL rompe el filtro y suena sin registrarse en el historial.
    }

    private fun shouldProcess(sbn: StatusBarNotification): Boolean {
        if (sbn.packageName == applicationContext.packageName) return false
        if (sbn.packageName == "com.android.systemui") return false
        if (sbn.isOngoing) return false
        return true
    }

    private fun silenceNotification(sbn: StatusBarNotification) {
        runCatching {
            cancelNotification(sbn.key)
            Log.d(TAG, "Silenciada notificación de ${sbn.packageName}")
        }.onFailure { e ->
            Log.w(TAG, "No se pudo silenciar la notificación", e)
        }
    }

    private fun isFilterEnabled(): Boolean {
        // TODO Fase 5: leer desde DataStore respetando el switch del Dashboard.
        return true
    }

    companion object {
        private const val TAG = "NotificationKeeper"
    }
}
