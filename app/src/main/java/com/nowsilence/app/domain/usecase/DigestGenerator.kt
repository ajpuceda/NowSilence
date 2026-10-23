package com.nowsilence.app.domain.usecase

import com.nowsilence.app.data.local.NotificationEntity
import com.nowsilence.app.domain.model.NotificationPriority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Optimizador de resúmenes diarios de NowSilence.
 *
 * Toma las notificaciones NORMAL/LOW del día y construye un prompt
 * estructurado para el LLM local (Google AI Edge), pidiendo un
 * resumen ejecutivo en viñetas. Incluye un generador offline de
 * respaldo mientras el modelo no esté cargado.
 */
class DigestGenerator {

    fun generateDailySummary(notifications: List<NotificationEntity>): String {
        if (notifications.isEmpty()) return "Sin notificaciones silenciadas hoy. Disfruta del silencio."

        val prompt = buildPrompt(notifications)
        // Con el LLM local cargado:
        // return llm.generateResponse(prompt)
        // Fase 4: respaldo offline determinista hasta activar el modelo.
        return generateOfflineSummary(notifications)
    }

    fun buildPrompt(notifications: List<NotificationEntity>): String {
        val formatted = notifications.joinToString("\n") {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.timestamp))
            "- [${it.category}] ${it.packageName} ($time): ${it.title} — ${it.message}"
        }
        return """
            Eres el asistente de NowSilence. A continuación recibes las notificaciones
            que fueron silenciadas hoy en el teléfono del usuario. Tu tarea:
            1. Extraer los puntos clave agrupados por categoría.
            2. Redactar un resumen ejecutivo breve en formato de viñetas.
            3. Máximo 5 viñetas, una línea por viñeta, tono neutro y calmado.
            Responde solo con las viñetas, sin introducción ni despedida.

            Notificaciones silenciadas hoy:
            $formatted
        """.trimIndent()
    }

    private fun generateOfflineSummary(notifications: List<NotificationEntity>): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return notifications
            .groupBy { it.category }
            .map { (category, items) ->
                val preview = items.first()
                val time = dateFormat.format(Date(preview.timestamp))
                "• $category: ${items.size} silenciadas. Ej: ${preview.title} ($time)"
            }
            .joinToString("\n")
    }

    private fun NotificationEntity.isSummarizable(): Boolean =
        category == NotificationPriority.NORMAL.name || category == NotificationPriority.LOW.name
}
