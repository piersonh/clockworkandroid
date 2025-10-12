package com.wordco.clockworkandroid

import android.app.Application

class MainApplication : Application() {

    lateinit var appContainer : AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = ProductionContainer(this)
    }
}