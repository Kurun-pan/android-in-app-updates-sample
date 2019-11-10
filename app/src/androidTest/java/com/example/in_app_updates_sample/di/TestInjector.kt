package com.example.in_app_updates_sample.di

import androidx.test.platform.app.InstrumentationRegistry
import com.example.in_app_updates_sample.UpdatesApplication

object TestInjector {

    fun inject(): TestAppComponent {

        val application = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as UpdatesApplication

        return DaggerTestAppComponent.factory()
            .create(application)
            .also { it.inject(application) } as TestAppComponent
    }
}
