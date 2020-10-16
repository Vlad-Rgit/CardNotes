package com.example.cardnotes.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cardnotes.R
import com.example.cardnotes.utils.LocaleHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        LocaleHelper.overrideLocaleFromAppContext(this)
    }
}