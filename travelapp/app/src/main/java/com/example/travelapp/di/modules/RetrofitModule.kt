package com.example.travelapp.di.modules

import com.example.common.data.data_sources.CityDataSource
import com.example.travelapp.conf.Config
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit()
        = Retrofit.Builder()
        .baseUrl(Config.baseUrl)
        .addConverterFactory(Json.asConverterFactory(
            MediaType.get("application/json")))
        .build()

    @Provides
    @Singleton
    fun provideCityDataSource(retrofit: Retrofit)
        = retrofit.create(CityDataSource::class.java)
}