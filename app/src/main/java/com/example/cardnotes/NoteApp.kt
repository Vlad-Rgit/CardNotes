package com.example.cardnotes

import android.app.Application
import android.text.format.DateFormat
import java.lang.IllegalStateException
import java.util.*

class NoteApp: Application() {

    companion object {

        @JvmStatic
        private lateinit var instance: NoteApp

        @JvmStatic
        fun getAppInstance(): NoteApp {
            if(!::instance.isInitialized) {
                throw IllegalStateException(
                    "Application is not yet initialized")
            }
            return instance
        }

        @JvmStatic
        fun is24hourFormat(): Boolean {
            return DateFormat.is24HourFormat(getAppInstance()
                .applicationContext)
        }

        @JvmStatic
        fun getLocale(): Locale {
            return getAppInstance().applicationContext.resources
                .configuration.locale
        }
    }

    override fun onCreate() {

        val preferences = getSharedPreferences(PreferencesName, MODE_PRIVATE)

        if(preferences.contains("language")) {
            val config = resources.configuration
            val displayMetrics = resources.displayMetrics
            config.locale = Locale(preferences.getString("language", ""))
            resources.updateConfiguration(config, displayMetrics)
        }
        super.onCreate()

        instance = this
    }


}