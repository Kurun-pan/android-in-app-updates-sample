package com.example.in_app_updates_sample.di

import com.example.in_app_updates_sample.UpdatesApplication
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
object TestAppModule {

    @Singleton
    @Provides
    fun providesFakeInAppUpdateManager(application: UpdatesApplication): FakeAppUpdateManager {

        return FakeAppUpdateManager(application)
    }

    @Provides
    fun providesInAppUpdateManager(fakeAppUpdateManager: FakeAppUpdateManager): AppUpdateManager {

        return fakeAppUpdateManager
    }

    @Provides
    fun providesPlayServiceExecutor(): Executor {

        return Executor { it.run() }
    }
}
