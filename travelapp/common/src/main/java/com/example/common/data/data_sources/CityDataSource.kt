package com.example.common.data.data_sources

import com.example.common.domain.City
import retrofit2.http.GET

interface CityDataSource {
    @GET("city")
    suspend fun getAll(): List<City>
}