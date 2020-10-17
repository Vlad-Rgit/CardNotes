package com.example.cardnotes.fragments



import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cardnotes.PreferencesLanguageKey
import com.example.cardnotes.PreferencesName
import com.example.cardnotes.R
import com.example.cardnotes.utils.LocaleHelper
import com.google.android.material.transition.MaterialFadeThrough
import java.util.*


class PreferencesFragment: Fragment() {

    private lateinit var languageCodes: Array<String>
    private lateinit var languageValues: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageCodes = resources.getStringArray(
            R.array.languages_codes)

        languageValues = resources.getStringArray(
            R.array.languages_values)

        enterTransition = MaterialFadeThrough()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
            R.layout.preferences_fragment, container, false)


        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, languageValues)

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        val spinnerLanguages = view.findViewById<Spinner>(
            R.id.spinner_language)

        spinnerLanguages.adapter = adapter
        spinnerLanguages.setSelection(getCurrentPos())

        spinnerLanguages.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(position != getCurrentPos())
                        changeAndSaveLocale(languageCodes[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        return view
    }


    @SuppressLint("ApplySharedPref")
    private fun changeAndSaveLocale(langCode: String) {
        val preferences = requireContext()
            .getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

        preferences.edit()
            .putString(PreferencesLanguageKey, langCode)
            .commit()

        LocaleHelper.updateAppLocale(Locale(langCode))
        requireActivity().recreate()
    }

    private fun getCurrentPos(): Int{
        val preferences = requireContext()
            .getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

        val currentCode = preferences.getString(PreferencesLanguageKey, "")

        return languageCodes.indexOf(currentCode)
    }

}