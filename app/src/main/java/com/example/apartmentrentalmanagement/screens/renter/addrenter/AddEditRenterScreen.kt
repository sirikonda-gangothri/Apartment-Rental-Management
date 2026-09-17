package com.example.apartmentrentalmanagement.screens.renter.addrenter

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apartmentrentalmanagement.screens.renter.RenterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

// =============================================================
// PROPERTY MODEL FOR RENTER SCREEN
// =============================================================

data class RenterProperty(
    val id: String = "",
    val name: String = ""
)

// =============================================================
// FLAT MODEL FOR RENTER SCREEN
// =============================================================

data class RenterFlat(
    val id: String = "",
    val flatNumber: String = "",
    val occupancyStatus: String = "VACANT",
    val monthlyRent: Double = 0.0
)

// =============================================================
// ADD / EDIT RENTER SCREEN
// =============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRenterScreen(
    renterId: String?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    renterViewModel: RenterViewModel = viewModel()
) {

    // ---------------------------------------------------------
    // FORM FIELDS
    // ---------------------------------------------------------

    var name by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var monthlyRent by remember {
        mutableStateOf("")
    }

    var advanceAmount by remember {
        mutableStateOf("")
    }

    var leaseStartDate by remember {
        mutableStateOf("")
    }

    var leaseEndDate by remember {
        mutableStateOf("")
    }

    // ---------------------------------------------------------
    // OPTIONAL END DATE
    // ---------------------------------------------------------

    var noFixedEndDate by remember {
        mutableStateOf(false)
    }

    // ---------------------------------------------------------
    // PROPERTY / FLAT DATA
    // ---------------------------------------------------------

    var properties by remember {
        mutableStateOf<List<RenterProperty>>(emptyList())
    }

    var flats by remember {
        mutableStateOf<List<RenterFlat>>(emptyList())
    }

    var selectedProperty by remember {
        mutableStateOf<RenterProperty?>(null)
    }

    var selectedFlat by remember {
        mutableStateOf<RenterFlat?>(null)
    }

    // ---------------------------------------------------------
    // BUILDING SEARCH
    // ---------------------------------------------------------

    var propertySearchQuery by remember {
        mutableStateOf("")
    }

    // ---------------------------------------------------------
    // FLAT SEARCH
    // ---------------------------------------------------------

    var flatSearchQuery by remember {
        mutableStateOf("")
    }

    // ---------------------------------------------------------
    // DROPDOWN STATES
    // ---------------------------------------------------------

    var propertyExpanded by remember {
        mutableStateOf(false)
    }

    var flatExpanded by remember {
        mutableStateOf(false)
    }

    // ---------------------------------------------------------
    // VIEWMODEL STATE
    // ---------------------------------------------------------

    val isSaving by renterViewModel.isSaving.collectAsState()

    val errorMessage by renterViewModel.errorMessage.collectAsState()

    val selectedRenter by renterViewModel.selectedRenter.collectAsState()

    // =========================================================
    // LOAD PROPERTIES
    // =========================================================

    LaunchedEffect(Unit) {

        val uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: return@LaunchedEffect

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .collection("properties")
            .get()
            .addOnSuccessListener { snapshot ->

                properties = snapshot.documents.map { document ->

                    RenterProperty(
                        id = document.id,

                        // IMPORTANT:
                        // AddPropertyScreen saves "propertyName"
                        name =
                            document.getString("propertyName")
                                ?: ""
                    )
                }
            }
    }

    // =========================================================
    // LOAD EXISTING RENTER WHEN EDITING
    // =========================================================

    LaunchedEffect(renterId) {

        if (renterId != null) {

            renterViewModel.getRenter(renterId)
        }
    }

    // =========================================================
    // FILL FORM WITH EXISTING RENTER
    // =========================================================

    LaunchedEffect(selectedRenter, properties) {

        val renter = selectedRenter

        if (
            renterId != null &&
            renter != null &&
            properties.isNotEmpty()
        ) {

            name = renter.name

            phone = renter.phone

            email = renter.email

            monthlyRent =
                renter.monthlyRent.toString()

            advanceAmount =
                renter.advanceAmount.toString()

            leaseStartDate =
                renter.leaseStartDate

            leaseEndDate =
                renter.leaseEndDate

            selectedProperty =
                properties.find {
                    it.id == renter.propertyId
                }

            // If the existing renter has no end date,
            // consider it an open-ended lease.
            noFixedEndDate =
                renter.leaseEndDate.isBlank()
        }
    }

    // =========================================================
    // LOAD FLATS WHEN PROPERTY IS SELECTED
    // =========================================================

    LaunchedEffect(selectedProperty) {

        val property =
            selectedProperty
                ?: return@LaunchedEffect

        val uid =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid
                ?: return@LaunchedEffect

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .collection("properties")
            .document(property.id)
            .collection("flats")
            .get()
            .addOnSuccessListener { snapshot ->

                flats =
                    snapshot.documents.map { document ->

                        RenterFlat(

                            id =
                                document.id,

                            flatNumber =
                                document
                                    .getString("flatNumber")
                                    ?: "",

                            occupancyStatus =
                                document
                                    .getString("occupancyStatus")
                                    ?: "VACANT",

                            monthlyRent =
                                document
                                    .getDouble("monthlyRent")
                                    ?: 0.0
                        )
                    }

                // -------------------------------------------------
                // If editing, select renter's existing flat
                // -------------------------------------------------

                if (selectedRenter != null) {

                    selectedFlat =
                        flats.find {

                            it.id ==
                                    selectedRenter!!.flatId
                        }
                }
            }
    }

    // =========================================================
    // FILTER BUILDINGS BASED ON SEARCH
    // =========================================================

    val filteredProperties =
        properties.filter { property ->

            property.name.contains(
                propertySearchQuery,
                ignoreCase = true
            )
        }

    // =========================================================
    // FILTER FLATS BASED ON SEARCH
    // =========================================================

    val availableFlats =
        flats.filter { flat ->

            flat.occupancyStatus.equals(
                "VACANT",
                ignoreCase = true
            ) ||
                    flat.id ==
                    selectedRenter?.flatId
        }

    val filteredFlats =
        availableFlats.filter { flat ->

            flat.flatNumber.contains(
                flatSearchQuery,
                ignoreCase = true
            )
        }

    // =========================================================
    // SCREEN
    // =========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            if (renterId == null) {
                                "Add Renter"
                            } else {
                                "Edit Renter"
                            }
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // NAME
            // =================================================

            OutlinedTextField(

                value = name,

                onValueChange = {
                    name = it
                },

                label = {
                    Text("Name")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            // =================================================
            // PHONE
            // =================================================

            OutlinedTextField(

                value = phone,

                onValueChange = {

                    phone = it
                },

                label = {
                    Text("Phone")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            // =================================================
            // EMAIL
            // =================================================

            OutlinedTextField(

                value = email,

                onValueChange = {

                    email = it
                },

                label = {
                    Text("Email")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            // =================================================
            // BUILDING DROPDOWN
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    propertyExpanded,

                onExpandedChange = {

                    propertyExpanded =
                        !propertyExpanded
                }
            ) {

                OutlinedTextField(

                    value =
                        if (propertyExpanded) {
                            propertySearchQuery
                        } else {
                            selectedProperty?.name
                                ?: ""
                        },

                    onValueChange = {

                        propertySearchQuery = it

                        propertyExpanded = true
                    },

                    label = {
                        Text("Building")
                    },

                    placeholder = {

                        Text(
                            if (propertyExpanded) {
                                "Search building..."
                            } else {
                                "Select building"
                            }
                        )
                    },

                    readOnly =
                        !propertyExpanded,

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    propertyExpanded
                            )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),

                    singleLine = true
                )

                ExposedDropdownMenu(

                    expanded =
                        propertyExpanded,

                    onDismissRequest = {

                        propertyExpanded =
                            false

                        propertySearchQuery =
                            ""
                    }
                ) {

                    if (
                        filteredProperties.isEmpty()
                    ) {

                        DropdownMenuItem(

                            text = {

                                Text(
                                    "No buildings found"
                                )
                            },

                            onClick = {}
                        )

                    } else {

                        filteredProperties
                            .forEach { property ->

                                DropdownMenuItem(

                                    text = {

                                        Text(
                                            property.name
                                        )
                                    },

                                    onClick = {

                                        selectedProperty =
                                            property

                                        selectedFlat =
                                            null

                                        flats =
                                            emptyList()

                                        flatSearchQuery =
                                            ""

                                        propertySearchQuery =
                                            ""

                                        propertyExpanded =
                                            false
                                    }
                                )
                            }
                    }
                }
            }

            // =================================================
            // FLAT DROPDOWN
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    flatExpanded,

                onExpandedChange = {

                    if (
                        selectedProperty != null
                    ) {

                        flatExpanded =
                            !flatExpanded
                    }
                }
            ) {

                OutlinedTextField(

                    value =
                        if (flatExpanded) {
                            flatSearchQuery
                        } else {
                            selectedFlat?.flatNumber
                                ?: ""
                        },

                    onValueChange = {

                        flatSearchQuery = it

                        flatExpanded = true
                    },

                    label = {
                        Text("Flat")
                    },

                    placeholder = {

                        Text(
                            if (
                                selectedProperty == null
                            ) {
                                "Select building first"
                            } else if (
                                flatExpanded
                            ) {
                                "Search flat number..."
                            } else {
                                "Select flat"
                            }
                        )
                    },

                    readOnly =
                        !flatExpanded,

                    enabled =
                        selectedProperty != null,

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    flatExpanded
                            )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),

                    singleLine = true
                )

                ExposedDropdownMenu(

                    expanded =
                        flatExpanded,

                    onDismissRequest = {

                        flatExpanded =
                            false

                        flatSearchQuery =
                            ""
                    }
                ) {

                    if (filteredFlats.isEmpty()) {

                        DropdownMenuItem(

                            text = {

                                Text(
                                    if (flats.isEmpty()) {
                                        "No flats available"
                                    } else {
                                        "No matching flats"
                                    }
                                )
                            },

                            onClick = {}
                        )

                    } else {

                        filteredFlats
                            .forEach { flat ->

                                DropdownMenuItem(

                                    text = {

                                        Text(
                                            flat.flatNumber
                                        )
                                    },

                                    onClick = {

                                        selectedFlat =
                                            flat

                                        flatSearchQuery =
                                            ""

                                        flatExpanded =
                                            false

                                        // ---------------------------------
                                        // Automatically use the flat's
                                        // default monthly rent.
                                        //
                                        // Only do this for a NEW renter.
                                        // When editing, preserve the
                                        // renter's existing agreed rent.
                                        // ---------------------------------

                                        if (
                                            renterId == null &&
                                            flat.monthlyRent > 0
                                        ) {

                                            monthlyRent =
                                                flat.monthlyRent
                                                    .toString()
                                        }
                                    }
                                )
                            }
                    }
                }
            }

            // =================================================
            // MONTHLY RENT
            // =================================================

            OutlinedTextField(

                value = monthlyRent,

                onValueChange = {

                    monthlyRent = it
                },

                label = {
                    Text("Agreed Monthly Rent")
                },

                supportingText = {

//                    Text(
//                        "Automatically filled from the flat's rent. You can change it if needed."
//                    )
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            // =================================================
            // ADVANCE
            // =================================================

            OutlinedTextField(

                value = advanceAmount,

                onValueChange = {

                    advanceAmount = it
                },

                label = {
                    Text("Advance Amount")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            // =================================================
            // LEASE START DATE
            // =================================================

            DateField(

                label =
                    "Lease Start Date",

                value =
                    leaseStartDate,

                onDateSelected = {

                    leaseStartDate =
                        it
                }
            )

            // =================================================
            // LEASE END DATE
            // =================================================

            DateField(

                label =
                    "Lease End Date",

                value =
                    if (noFixedEndDate) {
                        ""
                    } else {
                        leaseEndDate
                    },

                onDateSelected = {

                    leaseEndDate =
                        it

                    noFixedEndDate =
                        false
                }
            )

            // =================================================
            // NO FIXED END DATE
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Checkbox(

                    checked =
                        noFixedEndDate,

                    onCheckedChange = {

                        noFixedEndDate =
                            it

                        if (it) {

                            leaseEndDate =
                                ""
                        }
                    }
                )

                Text(
                    text =
                        "No fixed end date"
                )
            }

            // =================================================
            // ERROR MESSAGE
            // =================================================

            if (errorMessage != null) {

                Text(

                    text =
                        errorMessage ?: "",

                    color =
                        MaterialTheme
                            .colorScheme
                            .error,

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }

            // =================================================
            // SAVE BUTTON
            // =================================================

            Button(

                onClick = {

                    val property =
                        selectedProperty

                    val flat =
                        selectedFlat

                    // -----------------------------------------
                    // BASIC VALIDATION
                    // -----------------------------------------

                    if (name.isBlank()) {
                        return@Button
                    }

                    if (phone.isBlank()) {
                        return@Button
                    }

                    if (property == null) {
                        return@Button
                    }

                    if (flat == null) {
                        return@Button
                    }

                    // -----------------------------------------
                    // SAVE RENTER
                    // -----------------------------------------

                    renterViewModel.saveRenter(

                        renterId =
                            renterId,

                        name =
                            name.trim(),

                        phone =
                            phone.trim(),

                        email =
                            email.trim(),

                        propertyId =
                            property.id,

                        propertyName =
                            property.name,

                        flatId =
                            flat.id,

                        flatNumber =
                            flat.flatNumber,

                        monthlyRent =
                            monthlyRent
                                .toDoubleOrNull()
                                ?: 0.0,

                        advanceAmount =
                            advanceAmount
                                .toDoubleOrNull()
                                ?: 0.0,

                        leaseStartDate =
                            leaseStartDate,

                        leaseEndDate =
                            if (noFixedEndDate) {
                                ""
                            } else {
                                leaseEndDate
                            },

                        onSuccess =
                            onSaveSuccess
                    )
                },

                enabled =
                    !isSaving,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                if (isSaving) {

                    CircularProgressIndicator()

                } else {

                    Text(
                        text =
                            if (renterId == null) {
                                "Save Renter"
                            } else {
                                "Save Changes"
                            }
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }
}

// =============================================================
// DATE FIELD
// =============================================================

@Composable
private fun DateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            label = {
                Text(label)
            },
            placeholder = {
                Text("Select date")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Transparent clickable layer over the text field
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {

                    val calendar = Calendar.getInstance()

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->

                            val formatted =
                                "%02d/%02d/%04d".format(
                                    day,
                                    month + 1,
                                    year
                                )

                            onDateSelected(formatted)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        )
    }
}