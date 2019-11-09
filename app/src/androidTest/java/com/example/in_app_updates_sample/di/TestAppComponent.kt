package com.example.in_app_updates_sample.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = [ActivityModule::class, TestAppModule::class, AndroidInjectionModule::class])
interface TestAppComponent: AndroidInjector<TestApplication> {

    @Component.Builder
    interface Builder {

        fun appModule(appModule: TestAppModule): Builder

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AndroidInjector<TestApplication>
    }
}