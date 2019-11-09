package com.example.in_app_updates_sample.di

import android.app.Application
import com.example.in_app_updates_sample.UpdateApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = [AppModule::class, ActivityModule::class, AndroidInjectionModule::class])
interface AppComponent: AndroidInjector<UpdateApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AndroidInjector<UpdateApplication>
    }
}