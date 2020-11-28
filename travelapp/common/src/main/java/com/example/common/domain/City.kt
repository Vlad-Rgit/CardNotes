package com.example.common.domain

import kotlinx.serialization.Serializable


@Serializable
data class City(
    val id: Int,
    val name: String
)