package com.example.cardnotes.utils

import android.content.Context
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import com.example.cardnotes.NoteApp
import com.example.cardnotes.PreferencesLanguageKey
import com.example.cardnotes.PreferencesName
import java.util.*

object LocaleHelper {

    fun overrideLocaleFromAppContext(themeWrapper: ContextThemeWrapper) {
        val locale = NoteApp.getLocale()
        val conf = Configuration()
        conf.setLocale(locale)
        themeWrapper.applyOverrideConfiguration(conf)
    }

    fun updateLocale(context: Context, locale: Locale): Context {
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun updateAppLocale(locale: Locale) {
        val appContext = NoteApp.getAppInstance().applicationContext
        val conf = appContext.resources.configuration
        conf.setLocale(locale)
        appContext.createConfigurationContext(conf)
    }

    fun setLocaleFromSharedPrefsIfExists(context: Context): Context {
        val currLangCode = getCurrLangFromSharedPrefsOrNull(context)
        return if(currLangCode != null) {
            val locale = Locale(currLangCode)
            return updateLocale(context, locale)
        }
        else {
            context
        }
    }

    fun getCurrLangFromSharedPrefsOrNull(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PreferencesName,
            Context.MODE_PRIVATE)
        return sharedPrefs.getString(PreferencesLanguageKey, null)
    }

}

