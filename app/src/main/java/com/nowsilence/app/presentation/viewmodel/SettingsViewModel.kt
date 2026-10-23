package com.nowsilence.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nowsilence.app.data.local.UserSettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: UserSettingsDataStore
) : ViewModel() {

    val usagePlan: StateFlow<String> = settingsDataStore.usagePlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "PERSONAL")

    val externalApiKey: StateFlow<String> = settingsDataStore.externalApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun setUsagePlan(plan: String) {
        viewModelScope.launch { settingsDataStore.setUsagePlan(plan) }
    }

    fun setExternalApiKey(apiKey: String) {
        viewModelScope.launch { settingsDataStore.setExternalApiKey(apiKey) }
    }
}
