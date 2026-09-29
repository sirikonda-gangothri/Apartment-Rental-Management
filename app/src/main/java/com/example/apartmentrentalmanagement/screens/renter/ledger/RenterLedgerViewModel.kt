package com.example.apartmentrentalmanagement.screens.renter.ledger

import androidx.lifecycle.ViewModel
import com.example.apartmentrentalmanagement.screens.rent.RentPayment
import com.example.apartmentrentalmanagement.screens.renter.Renter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RenterLedgerViewModel : ViewModel() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()


    // =========================================================
    // CURRENT USER
    // =========================================================

    private val userId: String?
        get() = auth.currentUser?.uid


    // =========================================================
    // RENTER
    // =========================================================

    private val _renter =
        MutableStateFlow<Renter?>(null)

    val renter: StateFlow<Renter?> =
        _renter.asStateFlow()


    // =========================================================
    // PAYMENTS
    // =========================================================

    private val _payments =
        MutableStateFlow<List<RentPayment>>(
            emptyList()
        )

    val payments: StateFlow<List<RentPayment>> =
        _payments.asStateFlow()


    // =========================================================
    // LOADING
    // =========================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // =========================================================
    // ERROR
    // =========================================================

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()


    // =========================================================
    // REQUEST STATUS
    // =========================================================

    private var renterLoaded = false

    private var paymentsLoaded = false


    // =========================================================
    // LOAD LEDGER
    // =========================================================

    fun loadLedger(
        renterId: String
    ) {

        val uid = userId

        if (uid == null) {

            _errorMessage.value =
                "User is not logged in"

            _isLoading.value = false

            return
        }


        // -----------------------------------------------------
        // RESET REQUEST STATUS
        // -----------------------------------------------------

        renterLoaded = false
        paymentsLoaded = false

        _isLoading.value = true
        _errorMessage.value = null


        // =====================================================
        // LOAD RENTER
        // =====================================================

        firestore
            .collection("users")
            .document(uid)
            .collection("renters")
            .document(renterId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    _renter.value =
                        document
                            .toObject(Renter::class.java)
                            ?.copy(
                                id = document.id
                            )

                } else {

                    _renter.value = null

                    _errorMessage.value =
                        "Renter information not found"
                }

                renterLoaded = true

                checkLoadingComplete()
            }
            .addOnFailureListener { exception ->

                _renter.value = null

                _errorMessage.value =
                    exception.message
                        ?: "Failed to load renter"

                renterLoaded = true

                checkLoadingComplete()
            }


        // =====================================================
        // LOAD PAYMENTS
        // =====================================================

        firestore
            .collection("users")
            .document(uid)
            .collection("rentPayments")
            .whereEqualTo(
                "renterId",
                renterId
            )
            .get()
            .addOnSuccessListener { snapshot ->

                _payments.value =
                    snapshot.documents
                        .mapNotNull { document ->

                            document
                                .toObject(
                                    RentPayment::class.java
                                )
                                ?.copy(
                                    id = document.id
                                )
                        }
                        .sortedByDescending {
                            it.paymentDate
                        }

                paymentsLoaded = true

                checkLoadingComplete()
            }
            .addOnFailureListener { exception ->

                _payments.value =
                    emptyList()

                _errorMessage.value =
                    exception.message
                        ?: "Failed to load payment history"

                paymentsLoaded = true

                checkLoadingComplete()
            }
    }


    // =========================================================
    // CHECK LOADING
    // =========================================================

    private fun checkLoadingComplete() {

        if (
            renterLoaded &&
            paymentsLoaded
        ) {

            _isLoading.value = false
        }
    }


    // =========================================================
    // CLEAR ERROR
    // =========================================================

    fun clearError() {

        _errorMessage.value = null
    }
}