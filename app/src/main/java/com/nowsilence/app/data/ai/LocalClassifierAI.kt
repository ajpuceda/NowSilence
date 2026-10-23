package com.nowsilence.app.data.ai

import android.content.Context
import com.nowsilence.app.domain.model.NotificationPriority

/**
 * Gestor de IA On-Device para NowSilence.
 *
 * Estructura lista para el Google AI Edge SDK (MediaPipe LLM Inference).
 * Cuando el modelo ligero (Gemma 2B / Qwen 1.5B) esté disponible en
 * `assets/models/`, se desbloquea la inferencia real. Mientras tanto,
 * opera en modo heurístico local (100% offline, sin dependencias).
 *
 * La activación del SDK real requiere descomentar la dependencia
 * `libs.google.ai.edge` en app/build.gradle.kts y colocar el modelo
 * en app/src/main/assets/models/ (excluido del git por tamaño).
 */
class LocalClassifierAI(context: Context) {

    private val appContext = context.applicationContext

    @Volatile
    private var isModelLoaded = false

    fun initialize() {
        // Punto de carga del modelo AI Edge (MediaPipe LLM Inference).
        //
        // val options = LlmInferenceOptions.builder()
        //     .setModelPath("models/gemma-2b-it.task")
        //     .setMaxTokens(512)
        //     .build()
        // val llm = LlmInference.createFromOptions(appContext, options)
        // isModelLoaded = true
        //
        // Fase 3: el modelo aún no se incluye en el repo (binario grande).
        // El clasificador funciona en modo heurístico hasta que exista.
        isModelLoaded = false
    }

    fun release() {
        isModelLoaded = false
    }

    /**
     * Clasifica una notificación según su título y texto.
     *
     * Con modelo cargado: prompt del sistema embebido + inferencia LLM local.
     * Sin modelo: heurística local por palabras clave, siempre en el dispositivo.
     */
    fun classifyNotification(title: String, text: String): NotificationPriority {
        return if (isModelLoaded) {
            classifyWithLlm(title, text)
        } else {
            classifyWithHeuristics(title, text)
        }
    }

    private fun classifyWithLlm(title: String, text: String): NotificationPriority {
        // Prompt del sistema embebido para el modelo local.
        // Respuesta esperada: una sola palabra (CRITICAL | NORMAL | LOW).
        //
        // val prompt = """
        //     Eres un clasificador de notificaciones. Responde solo con una palabra.
        //     CRITICAL = urgente o importante (mensajes personales, salud, trabajo urgente).
        //     NORMAL = info útil no urgente (chats grupales, actualizaciones).
        //     LOW = spam, marketing, promociones, redes sociales.
        //     Notificación: [$title] $text
        // """.trimIndent()
        // val response = llm.generateResponse(prompt)
        // return parsePriority(response)
        return classifyWithHeuristics(title, text)
    }

    private fun parsePriority(response: String): NotificationPriority = when {
        response.contains("CRITICAL", ignoreCase = true) -> NotificationPriority.CRITICAL
        response.contains("LOW", ignoreCase = true) -> NotificationPriority.LOW
        else -> NotificationPriority.NORMAL
    }

    private fun classifyWithHeuristics(title: String, text: String): NotificationPriority {
        val content = "$title $text".lowercase()

        val criticalIndicators = listOf(
            "urgente", "urgent", "emergencia", "emergency",
            "hospital", "médic", "doctor", "salud",
            "llamada perdida", "missed call",
            "recordatorio", "reminder", "cita", "appointment",
            "transferencia", "transfer", "pago recibido", "nomina", "nómina"
        )
        val lowIndicators = listOf(
            "oferta", "promo", "descuento", "discount", "sale",
            "cupón", "coupon", "rebaja", "black friday",
            "te sigue", "follow", "likes", "me gusta",
            "nuevo video", "ha publicado", "tendencia", "trending",
            "publicidad", "ad", "newsletter", "suscrib"
        )

        return when {
            criticalIndicators.any { content.contains(it) } -> NotificationPriority.CRITICAL
            lowIndicators.any { content.contains(it) } -> NotificationPriority.LOW
            else -> NotificationPriority.NORMAL
        }
    }

    companion object {
        @Volatile
        private var instance: LocalClassifierAI? = null

        fun getInstance(context: Context): LocalClassifierAI =
            instance ?: synchronized(this) {
                instance ?: LocalClassifierAI(context).also {
                    it.initialize()
                    instance = it
                }
            }
    }
}
