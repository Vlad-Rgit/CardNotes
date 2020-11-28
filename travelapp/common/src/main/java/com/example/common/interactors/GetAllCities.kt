package com.example.common.interactors

import com.example.common.data.repos.CityRepo

class GetAllCities(private val cityRepo: CityRepo) {
    suspend operator fun invoke() = cityRepo.getAll()
}