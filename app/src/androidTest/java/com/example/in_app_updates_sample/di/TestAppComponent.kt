package com.example.in_app_updates_sample.di

import com.example.in_app_updates_sample.UpdatesApplication
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivityModule::class, TestAppModule::class])
interface TestAppComponent : AppComponent {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<UpdatesApplication>

    fun fakeAppUpdateManager(): FakeAppUpdateManager
}
