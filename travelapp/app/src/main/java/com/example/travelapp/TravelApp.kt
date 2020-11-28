package com.example.travelapp

import android.app.Application
import android.provider.ContactsContract
import com.example.travelapp.di.components.DaggerDataComponent
import com.example.travelapp.di.components.DataComponent
import dagger.Component

class TravelApp: Application() {

    companion object {
        private lateinit var _dataComponent: DataComponent
        val dataComponent: DataComponent
            get() = _dataComponent
    }

    override fun onCreate() {
        super.onCreate()
        _dataComponent = DaggerDataComponent.create()
    }

}