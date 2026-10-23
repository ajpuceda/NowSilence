package com.nowsilence.app.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nowsilence.app.data.ai.LocalClassifierAI
import com.nowsilence.app.domain.model.CapturedNotification
import com.nowsilence.app.domain.model.NotificationPriority

/**
 * Listener de notificaciones de NowSilence.
 *
 * Intercepta cada notificación entrante en tiempo real, la clasifica
 * con la IA local y decide: silenciar (cancelar) o permitir.
 * Solo opera si el usuario ha activado el filtro (flag persistido
 * en DataStore; se conectará con el ViewModel de la Fase 4).
 */
class NotificationKeeperService : NotificationListenerService() {

    private val classifier by lazy { LocalClassifierAI.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        classifier.initialize()
        Log.d(TAG, "NotificationKeeperService creado")
    }

    override fun onDestroy() {
        classifier.release()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || !shouldProcess(sbn)) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val text = extras.getCharSequence("android.text")?.toString().orEmpty()
        val packageName = sbn.packageName ?: "unknown"

        val priority = classifier.classifyNotification(title, text)

        val captured = CapturedNotification(
            packageName = packageName,
            title = title,
            text = text,
            postedAtMillis = sbn.postTime,
            priority = priority
        )

        Log.d(TAG, "Notificación [$packageName] prioridad=$priority título=$title")

        if (priority == NotificationPriority.LOW && isFilterEnabled()) {
            silenceNotification(sbn)
        }
        // CRITICAL y NORMAL se permiten pasar; NORMAL quedará registrada
        // en el historial y LOW iría al resumen diario (persistencia en Fase 4).
    }

    private fun shouldProcess(sbn: StatusBarNotification): Boolean {
        // Ignorar notificaciones del propio sistema y de NowSilence
        if (sbn.packageName == applicationContext.packageName) return false
        if (sbn.packageName == "com.android.systemui") return false
        // Ignorar notificaciones en curso (llamadas, música, navegación)
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
        // Fase 4: leer desde DataStore de configuración del usuario.
        // Por ahora el filtro está activo por defecto tras conceder el permiso.
        return true
    }

    companion object {
        private const val TAG = "NotificationKeeper"
    }
}
