package com.example.in_app_updates_sample.di

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import dagger.Module
import dagger.Provides

@Module
class TestAppModule(private val appUpdateManager: FakeAppUpdateManager) {

    @Provides
    fun getUpdateManager(): AppUpdateManager {
        return appUpdateManager
    }
}
