package cz.muni.fi.pv239.drinkup.utils

import android.app.Application

class ApplicationContextProvider(): Application() {
    override fun onCreate() {
        super.onCreate()
    }
}