package com.nowsilence.app

import android.app.Application
import com.nowsilence.app.di.dataModule
import com.nowsilence.app.di.domainModule
import com.nowsilence.app.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NowSilenceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NowSilenceApplication)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
