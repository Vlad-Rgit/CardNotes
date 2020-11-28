package com.example.common.data.repos

import com.example.common.data.data_sources.CityDataSource

class CityRepo(private val cityDataSource: CityDataSource) {
    suspend fun getAll() = cityDataSource.getAll()
}