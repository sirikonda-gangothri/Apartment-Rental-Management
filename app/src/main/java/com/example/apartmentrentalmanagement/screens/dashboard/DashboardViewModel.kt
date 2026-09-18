package com.example.apartmentrentalmanagement.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.apartmentrentalmanagement.screens.building.Flat
import com.example.apartmentrentalmanagement.screens.renter.Renter
import com.example.apartmentrentalmanagement.screens.rent.RentPayment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

data class DashboardAnalytics(
    val totalFlats: Int = 0,
    val occupiedFlats: Int = 0,
    val vacantFlats: Int = 0,
    val expectedRent: Double = 0.0,
    val collectedRent: Double = 0.0,
    val pendingRent: Double = 0.0,
    val occupancyPercentage: Double = 0.0,
    val collectionPercentage: Double = 0.0
)

class DashboardViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var analytics by mutableStateOf(DashboardAnalytics())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Firestore listeners
    private var propertiesListener: ListenerRegistration? = null
    private var rentersListener: ListenerRegistration? = null
    private var paymentsListener: ListenerRegistration? = null

    // One listener for flats inside each property
    private val flatListeners =
        mutableMapOf<String, ListenerRegistration>()

    // Flats grouped by property ID
    private val propertyFlats =
        mutableMapOf<String, List<Flat>>()

    // Current active renters
    private var activeRenters: List<Renter> = emptyList()

    // All rent payments
    private var payments: List<RentPayment> = emptyList()

    // Current property IDs
    private var currentPropertyIds: Set<String> = emptySet()

    // Used to know when initial data is loaded
    private var propertiesLoaded = false
    private var rentersLoaded = false
    private var paymentsLoaded = false

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            errorMessage = "User is not logged in"
            isLoading = false
            return
        }

        loadProperties(userId)
        loadRenters(userId)
        loadPayments(userId)
    }

    // ---------------------------------------------------------
    // PROPERTIES
    // ---------------------------------------------------------

    private fun loadProperties(userId: String) {

        propertiesListener?.remove()

        propertiesListener =
            db.collection("users")
                .document(userId)
                .collection("properties")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        errorMessage = error.message
                        propertiesLoaded = true
                        isLoading = false
                        return@addSnapshotListener
                    }

                    val propertyIds =
                        snapshot?.documents
                            ?.map { document ->
                                document.id
                            }
                            ?.toSet()
                            ?: emptySet()

                    currentPropertyIds = propertyIds

                    // Remove listeners for deleted properties
                    val deletedPropertyIds =
                        flatListeners.keys
                            .filter { propertyId ->
                                propertyId !in propertyIds
                            }

                    deletedPropertyIds.forEach { propertyId ->

                        flatListeners[propertyId]?.remove()

                        flatListeners.remove(propertyId)

                        propertyFlats.remove(propertyId)
                    }

                    // Listen to flats of every property
                    propertyIds.forEach { propertyId ->

                        if (!flatListeners.containsKey(propertyId)) {
                            listenToFlats(
                                userId = userId,
                                propertyId = propertyId
                            )
                        }
                    }

                    propertiesLoaded = true

                    calculateAnalytics()
                }
    }

    // ---------------------------------------------------------
    // FLATS
    // ---------------------------------------------------------

    private fun listenToFlats(
        userId: String,
        propertyId: String
    ) {

        val listener =
            db.collection("users")
                .document(userId)
                .collection("properties")
                .document(propertyId)
                .collection("flats")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        errorMessage = error.message

                        propertyFlats[propertyId] =
                            emptyList()

                        calculateAnalytics()

                        return@addSnapshotListener
                    }

                    val flats =
                        snapshot?.documents?.map { document ->

                            Flat(
                                id = document.id,

                                flatNumber =
                                    document.getString(
                                        "flatNumber"
                                    ) ?: "",

                                floor =
                                    document.getLong(
                                        "floor"
                                    )?.toInt() ?: 0,

                                flatType =
                                    document.getString(
                                        "flatType"
                                    ) ?: "",

                                monthlyRent =
                                    document.getDouble(
                                        "monthlyRent"
                                    ) ?: 0.0,

                                occupancyStatus =
                                    document.getString(
                                        "occupancyStatus"
                                    ) ?: "VACANT",

                                currentRenterId =
                                    document.getString(
                                        "currentRenterId"
                                    )
                            )

                        } ?: emptyList()

                    propertyFlats[propertyId] =
                        flats

                    calculateAnalytics()
                }

        flatListeners[propertyId] = listener
    }

    // ---------------------------------------------------------
    // RENTERS
    // ---------------------------------------------------------

    private fun loadRenters(userId: String) {

        rentersListener?.remove()

        rentersListener =
            db.collection("users")
                .document(userId)
                .collection("renters")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        errorMessage = error.message

                        rentersLoaded = true
                        isLoading = false

                        return@addSnapshotListener
                    }

                    val allRenters =
                        snapshot?.documents?.map { document ->

                            document.toObject(Renter::class.java)
                                ?.copy(
                                    id = document.id
                                )

                        }?.filterNotNull()
                            ?: emptyList()

                    // Dashboard uses only current/active renters
                    activeRenters =
                        allRenters.filter { renter ->
                            renter.active
                        }

                    rentersLoaded = true

                    calculateAnalytics()
                }
    }

    // ---------------------------------------------------------
    // RENT PAYMENTS
    // ---------------------------------------------------------

    private fun loadPayments(userId: String) {

        paymentsListener?.remove()

        paymentsListener =
            db.collection("users")
                .document(userId)
                .collection("rentPayments")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        errorMessage = error.message

                        paymentsLoaded = true
                        isLoading = false

                        return@addSnapshotListener
                    }

                    payments =
                        snapshot?.documents?.map { document ->

                            document.toObject(
                                RentPayment::class.java
                            )?.copy(
                                id = document.id
                            )

                        }?.filterNotNull()
                            ?: emptyList()

                    paymentsLoaded = true

                    calculateAnalytics()
                }
    }

    // ---------------------------------------------------------
    // CALCULATE DASHBOARD VALUES
    // ---------------------------------------------------------

    private fun calculateAnalytics() {

        // Combine flats from all properties
        val allFlats =
            currentPropertyIds.flatMap { propertyId ->
                propertyFlats[propertyId].orEmpty()
            }

        // -------------------------
        // FLAT COUNTS
        // -------------------------

        val totalFlats =
            allFlats.size

        val occupiedFlats =
            allFlats.count { flat ->

                flat.occupancyStatus.equals(
                    "OCCUPIED",
                    ignoreCase = true
                )
            }

        val vacantFlats =
            totalFlats - occupiedFlats

        // -------------------------
        // CURRENT MONTH/YEAR
        // -------------------------

        val calendar =
            Calendar.getInstance()

        val currentMonth =
            calendar.get(Calendar.MONTH) + 1

        val currentYear =
            calendar.get(Calendar.YEAR)

        // -------------------------
        // EXPECTED RENT
        // -------------------------

        val expectedRent =
            activeRenters.sumOf { renter ->
                renter.monthlyRent
            }

        // -------------------------
        // COLLECTED RENT
        // -------------------------

        /*
         * Only payments belonging to active renters
         * and the current month/year are included.
         *
         * This keeps Dashboard consistent with
         * your Rent Tracker logic.
         */

        val activeRenterIds =
            activeRenters
                .map { renter ->
                    renter.id
                }
                .toSet()

        val collectedRent =
            payments
                .filter { payment ->

                    payment.renterId in activeRenterIds &&
                            payment.month ==
                            currentMonth.toString() &&
                            payment.year ==
                            currentYear
                }
                .sumOf { payment ->
                    payment.amountPaid
                }

        // -------------------------
        // PENDING RENT
        // -------------------------

        val pendingRent =
            maxOf(
                expectedRent - collectedRent,
                0.0
            )

        // -------------------------
        // PERCENTAGES
        // -------------------------

        val occupancyPercentage =

            if (totalFlats > 0) {

                (occupiedFlats.toDouble() /
                        totalFlats.toDouble()) * 100

            } else {
                0.0
            }

        val collectionPercentage =

            if (expectedRent > 0) {

                (collectedRent /
                        expectedRent) * 100

            } else {
                0.0
            }

        // -------------------------
        // UPDATE UI STATE
        // -------------------------

        analytics =
            DashboardAnalytics(

                totalFlats =
                    totalFlats,

                occupiedFlats =
                    occupiedFlats,

                vacantFlats =
                    vacantFlats,

                expectedRent =
                    expectedRent,

                collectedRent =
                    collectedRent,

                pendingRent =
                    pendingRent,

                occupancyPercentage =
                    occupancyPercentage,

                collectionPercentage =
                    collectionPercentage
            )

        // Wait until all initial data is available
        val allFlatsLoaded =
            currentPropertyIds.all { propertyId ->
                propertyFlats.containsKey(propertyId)
            }

        isLoading =
            !(
                    propertiesLoaded &&
                            rentersLoaded &&
                            paymentsLoaded &&
                            allFlatsLoaded
                    )
    }

    // ---------------------------------------------------------
    // CLEANUP
    // ---------------------------------------------------------

    override fun onCleared() {

        propertiesListener?.remove()

        rentersListener?.remove()

        paymentsListener?.remove()

        flatListeners.values.forEach { listener ->
            listener.remove()
        }

        flatListeners.clear()

        propertyFlats.clear()

        super.onCleared()
    }
}