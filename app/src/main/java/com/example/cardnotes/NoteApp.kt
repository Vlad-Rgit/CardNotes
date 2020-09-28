package com.example.cardnotes

import android.app.Application
import java.lang.IllegalStateException

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
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}