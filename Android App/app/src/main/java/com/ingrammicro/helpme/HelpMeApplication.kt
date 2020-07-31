package com.ingrammicro.helpme

import android.app.Application
import android.content.Context
import com.ingrammicro.helpme.utils.SessionManager

class HelpMeApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = SessionManager(applicationContext)

    }

    companion object {
        @get:Synchronized
        @set:Synchronized
        var instance: HelpMeApplication? = null
        var prefs: SessionManager? = null

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

    }
}