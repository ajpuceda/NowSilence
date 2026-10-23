package com.nowsilence.app.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nowsilence.app.data.ai.LocalClassifierAI
import com.nowsilence.app.data.local.NotificationEntity
import com.nowsilence.app.data.local.UserSettingsDataStore
import com.nowsilence.app.domain.model.NotificationPriority
import com.nowsilence.app.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Listener de notificaciones de NowSilence.
 *
 * Respeta el estado del filtro persistido en DataStore: si está
 * desactivado, toda notificación pasa sin procesarse con la IA.
 * La extracción de datos es tolerante a fallos de permisos en
 * entornos con Perfil de Trabajo (Work Profile) administrado.
 */
class NotificationKeeperService : NotificationListenerService() {

    private val classifier: LocalClassifierAI by lazy { LocalClassifierAI.getInstance(this) }
    private val repository: NotificationRepository by lazy {
        org.koin.core.context.GlobalContext.get().get()
    }
    private val settingsDataStore: UserSettingsDataStore by lazy {
        UserSettingsDataStore(this)
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

        try {
            // Verificación inicial: si el filtro está desactivado, la
            // notificación pasa sin procesarse con la IA.
            if (!isFilterEnabled()) {
                Log.d(TAG, "Filtro desactivado: notificación de ${sbn.packageName} permitida")
                return
            }

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

            if (priority == NotificationPriority.LOW) {
                silenceNotification(sbn)
            }
            // CRITICAL rompe el filtro y suena sin registrarse en el historial.
        } catch (e: SecurityException) {
            // Escape seguro: Android puede bloquear la lectura de notificaciones
            // pertenecientes al contenedor cifrado de un Perfil de Trabajo (Work
            // Profile) administrado. La notificación pasa normalmente al sistema
            // del teléfono y suena, evitando cualquier crash de NowSilence.
            Log.d(TAG, "Notificación de ${sbn.packageName} no accesible (posible Work Profile): ${e.message}")
            return
        } catch (e: Exception) {
            // Cualquier otro fallo de extracción también debe ser tolerante:
            // la notificación pasa y el servicio sigue operativo.
            Log.w(TAG, "Error procesando notificación de ${sbn.packageName}: ${e.message}", e)
            return
        }
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

    private fun isFilterEnabled(): Boolean = runBlocking {
        settingsDataStore.isFilterEnabled.first()
    }

    companion object {
        private const val TAG = "NotificationKeeper"
    }
}
