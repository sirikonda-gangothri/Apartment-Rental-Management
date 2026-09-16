package com.example.apartmentrentalmanagement.screens.rent

data class RentPayment(
    val id: String = "",

    val renterId: String = "",
    val propertyId: String = "",
    val propertyName: String = "",
    val flatId: String = "",
    val flatNumber: String = "",

    val month: String = "",
    val year: Int = 0,

    val expectedRent: Double = 0.0,
    val amountPaid: Double = 0.0,

    val paymentDate: String = "",
    val paymentMethod: String = "",
    val notes: String = ""
)