package com.example.apartmentrentalmanagement.screens.renter

data class Renter(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",

    val propertyId: String = "",
    val propertyName: String = "",

    val flatId: String = "",
    val flatNumber: String = "",

    val monthlyRent: Double = 0.0,
    val advanceAmount: Double = 0.0,

    val leaseStartDate: String = "",
    val leaseEndDate: String = "",

    val active: Boolean = true,
    val moveOutDate: String = ""
)