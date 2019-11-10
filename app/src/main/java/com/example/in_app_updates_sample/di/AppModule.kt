package com.example.in_app_updates_sample.di

import com.example.in_app_updates_sample.UpdatesApplication
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.tasks.TaskExecutors
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor

@Module
object AppModule {

    @Provides
    fun providesInAppUpdateManager(application: UpdatesApplication): AppUpdateManager {

        return AppUpdateManagerFactory.create(application)
    }

    @Provides
    fun providesPlayServiceExecutor(): Executor {

        return TaskExecutors.MAIN_THREAD
    }
}
