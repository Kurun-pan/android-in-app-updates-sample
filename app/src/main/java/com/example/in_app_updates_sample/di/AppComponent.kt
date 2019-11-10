package com.example.in_app_updates_sample.di

import com.example.in_app_updates_sample.UpdatesApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = [AndroidInjectionModule::class, ActivityModule::class, AppModule::class])
interface AppComponent : AndroidInjector<UpdatesApplication> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<UpdatesApplication>
}
