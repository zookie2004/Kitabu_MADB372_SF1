package com.stadiolinks.kitabu

import android.app.Application
import com.stadiolinks.kitabu.di.AppContainer
import com.stadiolinks.kitabu.di.DefaultAppContainer

class KitabuApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
