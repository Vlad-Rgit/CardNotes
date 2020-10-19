package cf.feuerkrieg.cardnotes

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import cf.feuerkrieg.cardnotes.utils.LocaleHelper
import java.util.*

class NoteApp: Application() {

    companion object {

        private lateinit var instance: NoteApp

        fun getAppInstance(): NoteApp {
            if(!::instance.isInitialized) {
                throw IllegalStateException(
                    "Application is not yet initialized")
            }
            return instance
        }


        fun is24hourFormat(): Boolean {
            return DateFormat.is24HourFormat(getAppInstance()
                .applicationContext)
        }


        fun getLocale(): Locale {
            return getAppInstance().applicationContext.resources
                .configuration.locale
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper
            .setLocaleFromSharedPrefsOrSaveDefault(base!!))
    }


}