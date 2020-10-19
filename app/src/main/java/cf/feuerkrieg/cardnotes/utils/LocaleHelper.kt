package cf.feuerkrieg.cardnotes.utils

import android.content.Context
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.PreferencesLanguageKey
import cf.feuerkrieg.cardnotes.PreferencesName
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

    fun setLocaleFromSharedPrefsOrSaveDefault(context: Context): Context {
        val currLangCode = getCurrLangFromSharedPrefsOrNull(context)
        return if(currLangCode == null) {
            saveLocaleToSharedPrefs(context,
                context.resources.configuration.locale)
            context
        }
        else {
            val locale = Locale(currLangCode)
            return updateLocale(context, locale)
        }
    }

    fun saveLocaleToSharedPrefs(context: Context, locale: Locale) {
        val prefs = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(PreferencesLanguageKey, locale.language)
            .apply()
    }

    fun getCurrLangFromSharedPrefsOrNull(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PreferencesName,
            Context.MODE_PRIVATE)
        return sharedPrefs.getString(PreferencesLanguageKey, null)
    }

}

