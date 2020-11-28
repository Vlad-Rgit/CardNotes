package com.example.travelapp.presentation.mainscreen.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.City
import com.example.common.interactors.GetAllCities
import com.example.travelapp.TravelApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainFragmentViewModel: ViewModel() {

    @Inject
    lateinit var getAllCities: GetAllCities

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>>
        get() = _cities

    init {
        TravelApp.dataComponent.inject(this)
    }

    fun loadCities() {
        viewModelScope.launch {
            loadCitiesImpl()
        }
    }

    private suspend fun loadCitiesImpl()
        = withContext(Dispatchers.IO) {
        _cities.postValue(getAllCities())
    }
}