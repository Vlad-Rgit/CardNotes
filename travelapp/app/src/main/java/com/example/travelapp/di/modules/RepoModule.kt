package com.example.travelapp.di.modules

import com.example.common.data.data_sources.CityDataSource
import com.example.common.data.repos.CityRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {

    @Provides
    @Singleton
    fun provideCityRepo(cityDataSource: CityDataSource)
        = CityRepo(cityDataSource)


}