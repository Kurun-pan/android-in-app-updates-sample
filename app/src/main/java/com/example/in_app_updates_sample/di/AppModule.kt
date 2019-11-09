package com.example.in_app_updates_sample.di

import android.app.Application
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides

@Module
open class AppModule {

    @Provides
    fun getContext(app: Application): Context {

        return app.applicationContext
    }

    @Provides
    open fun getUpdateManager(context: Context): AppUpdateManager {

        return AppUpdateManagerFactory.create(context)
    }
}