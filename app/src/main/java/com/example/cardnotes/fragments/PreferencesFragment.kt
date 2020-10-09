package com.example.cardnotes.fragments



import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.cardnotes.PreferencesName
import com.example.cardnotes.R
import java.util.*


class PreferencesFragment: PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = PreferencesName
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val languagesPref = findPreference<ListPreference>("languages")

        languagesPref?.setOnPreferenceChangeListener { preference, newValue ->

            val newLanguage = newValue as String

            val config = resources.configuration
            val displayMetrics = resources.displayMetrics

            config.locale = Locale(newLanguage)
            resources.updateConfiguration(config, displayMetrics)

            true
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Toast.makeText(requireContext(), "Config changes", Toast.LENGTH_LONG).show()
        super.onConfigurationChanged(newConfig)
    }


}