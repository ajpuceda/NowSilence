package com.nowsilence.app.di

import com.nowsilence.app.data.ai.LocalClassifierAI
import com.nowsilence.app.data.local.NowSilenceDatabase
import com.nowsilence.app.data.local.UserSettingsDataStore
import com.nowsilence.app.data.repository.NotificationRepositoryImpl
import com.nowsilence.app.domain.repository.NotificationRepository
import com.nowsilence.app.domain.usecase.DigestGenerator
import com.nowsilence.app.presentation.viewmodel.DashboardViewModel
import com.nowsilence.app.presentation.viewmodel.DigestViewModel
import com.nowsilence.app.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single { NowSilenceDatabase.getInstance(androidContext()) }
    single { get<NowSilenceDatabase>().notificationDao() }
    single { UserSettingsDataStore(androidContext()) }
    single { LocalClassifierAI.getInstance(androidContext()) }
}

val domainModule = module {
    single<NotificationRepository> {
        NotificationRepositoryImpl(get())
    }
    single { DigestGenerator() }
}

val presentationModule = module {
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { DigestViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
