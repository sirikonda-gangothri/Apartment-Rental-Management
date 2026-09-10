package com.example.apartmentrentalmanagement.screens.building

data class Flat(
    val id: String = "",
    val flatNumber: String = "",
    val floor: Int = 0,
    val flatType: String = "",
    val monthlyRent: Double = 0.0,
    val occupancyStatus: String = "VACANT",
    val currentRenterId: String? = null
)