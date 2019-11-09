package com.example.in_app_updates_sample

import android.app.Activity
import android.app.Application
import com.example.in_app_updates_sample.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class UpdateApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .application(this)
            .build().inject(this)
    }

    override fun activityInjector() = activityDispatchingInjector
}
