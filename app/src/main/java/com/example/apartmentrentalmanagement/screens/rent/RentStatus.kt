package com.example.apartmentrentalmanagement.screens.rent

data class RentStatus(
    val renterId: String = "",
    val renterName: String = "",

    val propertyId: String = "",
    val propertyName: String = "",

    val flatId: String = "",
    val flatNumber: String = "",

    val month: String = "",
    val year: Int = 0,

    val expectedRent: Double = 0.0,
    val paidAmount: Double = 0.0,
    val pendingAmount: Double = 0.0,

    val paymentStatus: String = "PENDING"
)