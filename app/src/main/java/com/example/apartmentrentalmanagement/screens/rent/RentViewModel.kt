package com.example.apartmentrentalmanagement.screens.rent

import androidx.lifecycle.ViewModel
import com.example.apartmentrentalmanagement.screens.renter.Renter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class RentViewModel : ViewModel() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()


    // =========================================================
    // CURRENT USER
    // =========================================================

    private val userId: String?
        get() = auth.currentUser?.uid


    // =========================================================
    // SELECTED MONTH AND YEAR
    // =========================================================

    private val calendar = Calendar.getInstance()

    private val _selectedMonth =
        MutableStateFlow(
            calendar.get(Calendar.MONTH) + 1
        )

    val selectedMonth: StateFlow<Int> =
        _selectedMonth.asStateFlow()


    private val _selectedYear =
        MutableStateFlow(
            calendar.get(Calendar.YEAR)
        )

    val selectedYear: StateFlow<Int> =
        _selectedYear.asStateFlow()


    // =========================================================
    // RENT STATUS
    // =========================================================

    private val _rentStatuses =
        MutableStateFlow<List<RentStatus>>(emptyList())

    val rentStatuses: StateFlow<List<RentStatus>> =
        _rentStatuses.asStateFlow()


    // =========================================================
    // SUMMARY TOTALS
    // =========================================================

    private val _totalExpected =
        MutableStateFlow(0.0)

    val totalExpected: StateFlow<Double> =
        _totalExpected.asStateFlow()


    private val _totalCollected =
        MutableStateFlow(0.0)

    val totalCollected: StateFlow<Double> =
        _totalCollected.asStateFlow()


    private val _totalPending =
        MutableStateFlow(0.0)

    val totalPending: StateFlow<Double> =
        _totalPending.asStateFlow()


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
    // LOCAL DATA
    // =========================================================

    /*
     * Contains both:
     *
     * active renters
     * previous renters
     *
     * We need all renters for Rent Details and Rent Ledger.
     */
    private var allRenters: List<Renter> =
        emptyList()


    /*
     * Used only by Rent Tracker.
     */
    private val activeRenters: List<Renter>
        get() = allRenters.filter { it.active }


    /*
     * All payment transactions.
     */
    private var payments: List<RentPayment> =
        emptyList()


    // =========================================================
    // FIREBASE LISTENERS
    // =========================================================

    private var rentersListener: ListenerRegistration? = null

    private var paymentsListener: ListenerRegistration? = null


    // =========================================================
    // INITIALIZATION
    // =========================================================

    init {
        loadRentData()
    }


    // =========================================================
    // LOAD RENT DATA
    // =========================================================

    fun loadRentData() {

        val uid = userId

        if (uid == null) {
            _errorMessage.value = "User is not logged in"
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        loadAllRenters(uid)
        loadPayments(uid)
    }


    // =========================================================
    // LOAD RENTERS
    // =========================================================

    private fun loadAllRenters(uid: String) {

        rentersListener?.remove()

        rentersListener =
            firestore
                .collection("users")
                .document(uid)
                .collection("renters")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        _isLoading.value = false

                        _errorMessage.value =
                            error.message
                                ?: "Failed to load renters"

                        return@addSnapshotListener
                    }

                    allRenters =
                        snapshot
                            ?.documents
                            ?.mapNotNull { document ->

                                document
                                    .toObject(Renter::class.java)
                                    ?.copy(
                                        id = document.id
                                    )
                            }
                            ?: emptyList()

                    calculateRentStatuses()
                }
    }


    // =========================================================
    // LOAD PAYMENTS
    // =========================================================

    private fun loadPayments(uid: String) {

        paymentsListener?.remove()

        paymentsListener =
            firestore
                .collection("users")
                .document(uid)
                .collection("rentPayments")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        _isLoading.value = false

                        _errorMessage.value =
                            error.message
                                ?: "Failed to load payments"

                        return@addSnapshotListener
                    }

                    payments =
                        snapshot
                            ?.documents
                            ?.mapNotNull { document ->

                                document
                                    .toObject(RentPayment::class.java)
                                    ?.copy(
                                        id = document.id
                                    )
                            }
                            ?: emptyList()

                    calculateRentStatuses()
                }
    }


    // =========================================================
    // CALCULATE TRACKER STATUSES
    // =========================================================

    private fun calculateRentStatuses() {

        val month = _selectedMonth.value
        val year = _selectedYear.value

        val statuses =
            activeRenters.map { renter ->

                createRentStatus(
                    renter = renter,
                    month = month,
                    year = year
                )
            }

        _rentStatuses.value = statuses

        _totalExpected.value =
            statuses.sumOf { it.expectedRent }

        _totalCollected.value =
            statuses.sumOf { it.paidAmount }

        _totalPending.value =
            statuses.sumOf { it.pendingAmount }

        _isLoading.value = false
    }


    // =========================================================
    // CREATE ONE RENT STATUS
    // =========================================================

    private fun createRentStatus(
        renter: Renter,
        month: Int,
        year: Int
    ): RentStatus {

        val renterPayments =
            payments.filter { payment ->

                payment.renterId == renter.id &&
                        payment.month == month.toString() &&
                        payment.year == year
            }

        val paidAmount =
            renterPayments.sumOf { it.amountPaid }

        val expectedRent =
            renter.monthlyRent

        val pendingAmount =
            maxOf(
                expectedRent - paidAmount,
                0.0
            )

        val paymentStatus =
            when {

                expectedRent <= 0.0 ->
                    "PAID"

                paidAmount >= expectedRent ->
                    "PAID"

                paidAmount > 0.0 ->
                    "PARTIAL"

                else ->
                    "PENDING"
            }

        return RentStatus(
            renterId = renter.id,
            renterName = renter.name,

            propertyId = renter.propertyId,
            propertyName = renter.propertyName,

            flatId = renter.flatId,
            flatNumber = renter.flatNumber,

            month = month.toString(),
            year = year,

            expectedRent = expectedRent,
            paidAmount = paidAmount,
            pendingAmount = pendingAmount,

            paymentStatus = paymentStatus
        )
    }


    // =========================================================
    // CHANGE MONTH
    // =========================================================

    fun setMonth(
        month: Int,
        year: Int
    ) {

        _selectedMonth.value = month

        _selectedYear.value = year

        calculateRentStatuses()
    }


    // =========================================================
    // GET RENTER
    // =========================================================

    fun getRenter(
        renterId: String
    ): Renter? {

        return allRenters.find {
            it.id == renterId
        }
    }


    // =========================================================
    // GET ACTIVE RENTER
    // =========================================================

    fun getActiveRenter(
        renterId: String
    ): Renter? {

        return activeRenters.find {
            it.id == renterId
        }
    }


    // =========================================================
    // GET RENT STATUS
    // =========================================================

    fun getRentStatus(
        renterId: String
    ): RentStatus? {

        /*
         * First check the tracker list.
         *
         * This handles active renters.
         */
        val existingStatus =
            _rentStatuses.value.find {
                it.renterId == renterId
            }

        if (existingStatus != null) {
            return existingStatus
        }


        /*
         * If it wasn't found there, the renter may be
         * a previous/inactive renter.
         *
         * Create a status directly from that renter.
         */
        val renter =
            getRenter(renterId)
                ?: return null

        return createRentStatus(
            renter = renter,
            month = _selectedMonth.value,
            year = _selectedYear.value
        )
    }


    // =========================================================
    // GET PAYMENTS FOR RENTER
    // =========================================================

    fun getPaymentsForRenter(
        renterId: String
    ): List<RentPayment> {

        return payments
            .filter {
                it.renterId == renterId
            }
            .sortedByDescending {
                it.paymentDate
            }
    }


    // =========================================================
    // RECORD PAYMENT
    // =========================================================

    fun recordPayment(
        renter: Renter,
        amountPaid: Double,
        paymentDate: String,
        paymentMethod: String,
        notes: String,
        onSuccess: () -> Unit = {}
    ) {

        val uid = userId

        // -----------------------------------------------------
        // LOGIN CHECK
        // -----------------------------------------------------

        if (uid == null) {

            _errorMessage.value =
                "User is not logged in"

            return
        }


        // -----------------------------------------------------
        // AMOUNT CHECK
        // -----------------------------------------------------

        if (amountPaid <= 0.0) {

            _errorMessage.value =
                "Payment amount must be greater than 0"

            return
        }


        // -----------------------------------------------------
        // GET CURRENT RENT STATU
        // -----------------------------------------------------

        val currentStatus =
            createRentStatus(
                renter = renter,
                month = _selectedMonth.value,
                year = _selectedYear.value
            )


        // -----------------------------------------------------
        // CHECK PENDING AMOUNT
        // -----------------------------------------------------

        if (amountPaid > currentStatus.pendingAmount) {

            _errorMessage.value =
                "Payment cannot be greater than the pending amount"

            return
        }


        // -----------------------------------------------------
        // CREATE FIRESTORE DOCUMENT
        // -----------------------------------------------------

        val paymentDocument =
            firestore
                .collection("users")
                .document(uid)
                .collection("rentPayments")
                .document()


        // -----------------------------------------------------
        // CREATE PAYMENT
        // -----------------------------------------------------

        val payment =
            RentPayment(

                id = paymentDocument.id,

                renterId = renter.id,

                propertyId = renter.propertyId,
                propertyName = renter.propertyName,

                flatId = renter.flatId,
                flatNumber = renter.flatNumber,

                month =
                    _selectedMonth.value.toString(),

                year =
                    _selectedYear.value,

                expectedRent =
                    renter.monthlyRent,

                amountPaid =
                    amountPaid,

                paymentDate =
                    paymentDate,

                paymentMethod =
                    paymentMethod,

                notes =
                    notes.trim()
            )


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        paymentDocument
            .set(payment)
            .addOnSuccessListener {

                /*
                 * Firebase listener will normally update
                 * the payment list automatically.
                 *
                 * Recalculate here as well so the UI can
                 * update immediately.
                 */
                calculateRentStatuses()

                _errorMessage.value = null

                onSuccess()
            }
            .addOnFailureListener { exception ->

                _errorMessage.value =
                    exception.message
                        ?: "Failed to record payment"
            }
    }


    // =========================================================
    // CLEAR ERROR
    // =========================================================

    fun clearError() {

        _errorMessage.value = null
    }


    // =========================================================
    // CLEAN UP
    // =========================================================

    override fun onCleared() {

        super.onCleared()

        rentersListener?.remove()

        paymentsListener?.remove()
    }
}