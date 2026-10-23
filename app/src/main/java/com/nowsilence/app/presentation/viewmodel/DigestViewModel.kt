package com.nowsilence.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nowsilence.app.data.local.NotificationEntity
import com.nowsilence.app.domain.repository.NotificationRepository
import com.nowsilence.app.domain.usecase.DigestGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DigestUiState(
    val summary: String = "Sin notificaciones silenciadas hoy. Disfruta del silencio."
)

class DigestViewModel(
    private val repository: NotificationRepository,
    private val digestGenerator: DigestGenerator = DigestGenerator()
) : ViewModel() {

    val todayNotifications: StateFlow<List<NotificationEntity>> =
        repository.getTodayNotifications()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _summary = MutableStateFlow(DigestUiState())
    val summary: StateFlow<DigestUiState> = _summary

    init {
        viewModelScope.launch {
            todayNotifications.collect { notifications ->
                _summary.value = DigestUiState(
                    summary = digestGenerator.generateDailySummary(notifications)
                )
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}
