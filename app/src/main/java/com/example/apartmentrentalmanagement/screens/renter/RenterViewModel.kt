package com.example.apartmentrentalmanagement.screens.renter

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RenterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _renters =
        MutableStateFlow<List<Renter>>(emptyList())

    val renters: StateFlow<List<Renter>> =
        _renters.asStateFlow()

    private val _selectedRenter =
        MutableStateFlow<Renter?>(null)

    val selectedRenter: StateFlow<Renter?> =
        _selectedRenter.asStateFlow()

    private val _isSaving =
        MutableStateFlow(false)

    val isSaving: StateFlow<Boolean> =
        _isSaving.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private var renterListener: ListenerRegistration? = null

    private val userId: String?
        get() = auth.currentUser?.uid

    init {
        loadRenters()
    }

    // =========================================================
    // LOAD ALL RENTERS
    // =========================================================

    fun loadRenters() {

        val uid = userId ?: return

        renterListener?.remove()

        renterListener = firestore
            .collection("users")
            .document(uid)
            .collection("renters")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    _errorMessage.value =
                        error.message

                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val renterList =
                        snapshot.documents.mapNotNull { document ->

                            document
                                .toObject(Renter::class.java)
                                ?.copy(
                                    id = document.id
                                )
                        }

                    _renters.value =
                        renterList
                }
            }
    }

    // =========================================================
    // LOAD ONE RENTER
    // =========================================================

    fun getRenter(renterId: String) {

        val uid = userId ?: return

        firestore
            .collection("users")
            .document(uid)
            .collection("renters")
            .document(renterId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    _selectedRenter.value =
                        document
                            .toObject(Renter::class.java)
                            ?.copy(
                                id = document.id
                            )
                }
            }
            .addOnFailureListener {

                _errorMessage.value =
                    it.message
            }
    }

    // =========================================================
    // SAVE / UPDATE RENTER
    // =========================================================

    fun saveRenter(
        renterId: String?,
        name: String,
        phone: String,
        email: String,
        propertyId: String,
        propertyName: String,
        flatId: String,
        flatNumber: String,
        monthlyRent: Double,
        advanceAmount: Double,
        leaseStartDate: String,
        leaseEndDate: String,
        onSuccess: () -> Unit
    ) {

        val uid = userId

        if (uid == null) {

            _errorMessage.value =
                "User is not logged in"

            return
        }

        _isSaving.value = true
        _errorMessage.value = null

        val userReference =
            firestore
                .collection("users")
                .document(uid)

        val renterCollection =
            userReference
                .collection("renters")

        // ---------------------------------------------------------
        // Existing renter -> use existing document
        // New renter -> create new document ID
        // ---------------------------------------------------------

        val renterReference =
            if (renterId == null) {

                renterCollection.document()

            } else {

                renterCollection.document(renterId)
            }

        // ---------------------------------------------------------
        // New selected flat
        // ---------------------------------------------------------

        val newFlatReference =
            userReference
                .collection("properties")
                .document(propertyId)
                .collection("flats")
                .document(flatId)

        firestore.runTransaction { transaction ->

            // =====================================================
            // READ NEW FLAT
            // =====================================================

            val newFlatSnapshot =
                transaction.get(
                    newFlatReference
                )

            if (!newFlatSnapshot.exists()) {

                throw IllegalStateException(
                    "Selected flat does not exist"
                )
            }

            val newFlatStatus =
                newFlatSnapshot
                    .getString("occupancyStatus")
                    ?: "VACANT"

            val currentRenterId =
                newFlatSnapshot
                    .getString("currentRenterId")

            // =====================================================
            // LOAD EXISTING RENTER WHEN EDITING
            // =====================================================

            val existingRenter =
                if (renterId != null) {

                    transaction
                        .get(renterReference)
                        .toObject(Renter::class.java)

                } else {

                    null
                }

            // =====================================================
            // CHECK WHETHER THIS IS THE SAME FLAT
            // =====================================================

            val isSameFlat =
                existingRenter != null &&
                        existingRenter.flatId == flatId &&
                        existingRenter.propertyId == propertyId

            // =====================================================
            // CHECK IF NEW FLAT IS ALREADY OCCUPIED
            // =====================================================

            if (
                newFlatStatus.equals(
                    "OCCUPIED",
                    ignoreCase = true
                ) &&
                !isSameFlat &&
                currentRenterId != renterId
            ) {

                throw IllegalStateException(
                    "This flat is already occupied"
                )
            }

            // =====================================================
            // FIND OLD FLAT IF RENTER IS MOVING
            // =====================================================

            val oldFlatReference =

                if (
                    existingRenter != null &&
                    existingRenter.flatId.isNotBlank() &&
                    (
                            existingRenter.flatId != flatId ||
                                    existingRenter.propertyId != propertyId
                            )
                ) {

                    userReference
                        .collection("properties")
                        .document(
                            existingRenter.propertyId
                        )
                        .collection("flats")
                        .document(
                            existingRenter.flatId
                        )

                } else {

                    null
                }

            // =====================================================
            // CREATE UPDATED RENTER
            // =====================================================

            val renter = Renter(

                id =
                    renterReference.id,

                name =
                    name,

                phone =
                    phone,

                email =
                    email,

                propertyId =
                    propertyId,

                propertyName =
                    propertyName,

                flatId =
                    flatId,

                flatNumber =
                    flatNumber,

                monthlyRent =
                    monthlyRent,

                advanceAmount =
                    advanceAmount,

                leaseStartDate =
                    leaseStartDate,

                leaseEndDate =
                    leaseEndDate,

                active =
                    true,

                moveOutDate =
                    ""
            )

            // =====================================================
            // SAVE RENTER
            // =====================================================

            transaction.set(
                renterReference,
                renter
            )

            // =====================================================
            // MAKE NEW FLAT OCCUPIED
            // =====================================================

            transaction.update(

                newFlatReference,

                mapOf(

                    "occupancyStatus"
                            to "OCCUPIED",

                    "currentRenterId"
                            to renterReference.id
                )
            )

            // =====================================================
            // IF RENTER MOVED TO ANOTHER FLAT,
            // MAKE OLD FLAT VACANT
            // =====================================================

            if (oldFlatReference != null) {

                transaction.update(

                    oldFlatReference,

                    mapOf(

                        "occupancyStatus"
                                to "VACANT",

                        "currentRenterId"
                                to null
                    )
                )
            }

            null

        }.addOnSuccessListener {

            _isSaving.value = false

            onSuccess()

        }.addOnFailureListener {

            _isSaving.value = false

            _errorMessage.value =
                it.message
                    ?: "Unable to save renter"
        }
    }

    // =========================================================
    // END TENANCY / MOVE OUT
    // =========================================================

    fun endTenancy(
        renter: Renter,
        moveOutDate: String,
        onSuccess: () -> Unit
    ) {

        val uid = userId

        if (uid == null) {

            _errorMessage.value =
                "User is not logged in"

            return
        }

        _isSaving.value = true
        _errorMessage.value = null

        val userReference =
            firestore
                .collection("users")
                .document(uid)

        val renterReference =
            userReference
                .collection("renters")
                .document(renter.id)

        val flatReference =
            userReference
                .collection("properties")
                .document(renter.propertyId)
                .collection("flats")
                .document(renter.flatId)

        firestore.runTransaction { transaction ->

            // =====================================================
            // READ RENTER
            // =====================================================

            val renterSnapshot =
                transaction.get(
                    renterReference
                )

            if (!renterSnapshot.exists()) {

                throw IllegalStateException(
                    "Renter does not exist"
                )
            }

            // =====================================================
            // READ FLAT
            // =====================================================

            val flatSnapshot =
                transaction.get(
                    flatReference
                )

            if (!flatSnapshot.exists()) {

                throw IllegalStateException(
                    "Associated flat does not exist"
                )
            }

            // =====================================================
            // UPDATE RENTER
            // =====================================================

            transaction.update(

                renterReference,

                mapOf(

                    "active"
                            to false,

                    "moveOutDate"
                            to moveOutDate
                )
            )

            // =====================================================
            // MAKE FLAT VACANT
            // =====================================================

            transaction.update(

                flatReference,

                mapOf(

                    "occupancyStatus"
                            to "VACANT",

                    "currentRenterId"
                            to null
                )
            )

            null

        }.addOnSuccessListener {

            _isSaving.value = false

            onSuccess()

        }.addOnFailureListener {

            _isSaving.value = false

            _errorMessage.value =
                it.message
                    ?: "Unable to end tenancy"
        }
    }

    // =========================================================
    // CLEAR ERROR
    // =========================================================

    fun clearError() {

        _errorMessage.value = null
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    override fun onCleared() {

        super.onCleared()

        renterListener?.remove()
    }
}

