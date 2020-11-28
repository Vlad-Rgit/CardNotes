package com.example.travelapp.di.modules

import com.example.common.data.repos.CityRepo
import com.example.common.interactors.GetAllCities
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun provideGetAllCities(cityRepo: CityRepo)
        = GetAllCities(cityRepo)
}