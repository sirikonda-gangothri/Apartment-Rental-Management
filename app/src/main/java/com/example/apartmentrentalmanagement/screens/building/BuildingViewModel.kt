package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class BuildingViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var flats by mutableStateOf<List<Flat>>(emptyList())
        private set

    var property by mutableStateOf<Property?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var flatsListener: ListenerRegistration? = null

    fun loadFlats(propertyDocumentId: String) {

        val userId = auth.currentUser?.uid ?: return

        flatsListener?.remove()

        flatsListener = db.collection("users")
            .document(userId)
            .collection("properties")
            .document(propertyDocumentId)
            .collection("flats")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    errorMessage = error.message
                    return@addSnapshotListener
                }

                flats = snapshot?.documents?.map { document ->

                    Flat(
                        id = document.id,
                        flatNumber = document.getString("flatNumber") ?: "",
                        floor = document.getLong("floor")?.toInt() ?: 0,
                        flatType = document.getString("flatType") ?: "",
                        monthlyRent = document.getDouble("monthlyRent") ?: 0.0,
                        occupancyStatus =
                            document.getString("occupancyStatus") ?: "VACANT",
                        currentRenterId =
                            document.getString("currentRenterId")
                    )

                } ?: emptyList()
            }
    }

    fun loadFlat(
        propertyDocumentId: String,
        flatId: String,
        onLoaded: (Flat) -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("properties")
            .document(propertyDocumentId)
            .collection("flats")
            .document(flatId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val flat = Flat(
                        id = document.id,
                        flatNumber =
                            document.getString("flatNumber") ?: "",
                        floor =
                            document.getLong("floor")?.toInt() ?: 0,
                        flatType =
                            document.getString("flatType") ?: "",
                        monthlyRent =
                            document.getDouble("monthlyRent") ?: 0.0,
                        occupancyStatus =
                            document.getString("occupancyStatus")
                                ?: "VACANT",
                        currentRenterId =
                            document.getString("currentRenterId")
                    )

                    onLoaded(flat)
                }
            }
            .addOnFailureListener { exception ->

                errorMessage = exception.message
            }
    }

    fun loadProperty(propertyDocumentId: String) {

        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("properties")
            .document(propertyDocumentId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    property = Property(
                        id = document.id,
                        name = document.getString("propertyName") ?: "",
                        type = document.getString("propertyType") ?: "",
                        propertyId = document.getString("propertyId") ?: "",
                        location = document.getString("location") ?: "",
                        status = document.getString("status") ?: "",
                        floors = document.getLong("floors")?.toInt() ?: 0
                    )
                }
            }
            .addOnFailureListener { exception ->

                errorMessage = exception.message
            }
    }

    fun saveFlat(
        propertyDocumentId: String,
        flat: Flat,
        onSuccess: () -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        isSaving = true
        errorMessage = null

        val flatData = hashMapOf(
            "flatNumber" to flat.flatNumber,
            "floor" to flat.floor,
            "flatType" to flat.flatType,
            "monthlyRent" to flat.monthlyRent,
            "occupancyStatus" to flat.occupancyStatus,
            "currentRenterId" to flat.currentRenterId
        )

        val flatsCollection = db.collection("users")
            .document(userId)
            .collection("properties")
            .document(propertyDocumentId)
            .collection("flats")

        val task = if (flat.id.isBlank()) {

            flatsCollection.add(flatData)

        } else {

            flatsCollection
                .document(flat.id)
                .set(flatData)
        }

        task.addOnSuccessListener {

            isSaving = false
            onSuccess()

        }.addOnFailureListener { exception ->

            isSaving = false
            errorMessage = exception.message
        }
    }

    fun generateFlats(
        propertyDocumentId: String,
        flats: List<Flat>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            onFailure("User is not logged in.")
            return
        }

        if (flats.isEmpty()) {
            onFailure("No flats to generate.")
            return
        }

        isSaving = true
        errorMessage = null

        val flatsCollection = db.collection("users")
            .document(userId)
            .collection("properties")
            .document(propertyDocumentId)
            .collection("flats")

        val batch = db.batch()

        flats.forEach { flat ->

            val flatData = hashMapOf(
                "flatNumber" to flat.flatNumber,
                "floor" to flat.floor,
                "flatType" to flat.flatType,
                "monthlyRent" to flat.monthlyRent,
                "occupancyStatus" to "VACANT",
                "currentRenterId" to null
            )

            val documentReference = flatsCollection.document()

            batch.set(
                documentReference,
                flatData
            )
        }

        batch.commit()
            .addOnSuccessListener {

                isSaving = false
                onSuccess()

            }
            .addOnFailureListener { exception ->

                isSaving = false

                val message =
                    exception.message ?: "Failed to generate flats."

                errorMessage = message
                onFailure(message)
            }
    }

    fun clearError() {
        errorMessage = null
    }

    override fun onCleared() {
        super.onCleared()
        flatsListener?.remove()
    }
}