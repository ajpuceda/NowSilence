package com.nowsilence.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nowsilence.app.data.local.UserSettingsDataStore
import com.nowsilence.app.domain.repository.NotificationRepository
import com.nowsilence.app.domain.model.NotificationPriority
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val settingsDataStore: UserSettingsDataStore,
    private val repository: NotificationRepository
) : ViewModel() {

    val isFilterEnabled: StateFlow<Boolean> = settingsDataStore.isFilterEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val filterProfile: StateFlow<String> = settingsDataStore.filterProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "PERSONAL")

    val blockedTodayCount: StateFlow<Int> = repository.getTodayCountByCategory(
        NotificationPriority.LOW.name
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setFilterEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setFilterEnabled(enabled) }
    }

    fun setFilterProfile(profile: String) {
        viewModelScope.launch { settingsDataStore.setFilterProfile(profile) }
    }
}
