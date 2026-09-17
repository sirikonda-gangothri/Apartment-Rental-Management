package com.example.apartmentrentalmanagement.screens.renter

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterDetailsScreen(
    renterId: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onLedgerClick: () -> Unit,
    renterViewModel: RenterViewModel = viewModel(),
) {

    val renter by renterViewModel.selectedRenter.collectAsState()

    var showEndTenancyDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(renterId) {
        renterViewModel.getRenter(renterId)
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Renter Details")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (renter == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Loading renter...")
            }

        } else {

            val currentRenter = renter!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = currentRenter.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    text = "Phone: ${currentRenter.phone}"
                )

                Text(
                    text = "Email: ${currentRenter.email}"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Building: ${currentRenter.propertyName}"
                )

                Text(
                    text = "Flat: ${currentRenter.flatNumber}"
                )

                Text(
                    text = "Agreed Monthly Rent: ₹${currentRenter.monthlyRent}"
                )

                Text(
                    text = "Advance Amount: ₹${currentRenter.advanceAmount}"
                )

                Text(
                    text = "Lease Start: ${currentRenter.leaseStartDate}"
                )

                Text(
                    text = "Lease End: ${
                        if (currentRenter.leaseEndDate.isBlank()) {
                            "No fixed end date"
                        } else {
                            currentRenter.leaseEndDate
                        }
                    }"
                )

                Text(
                    text = if (currentRenter.active) {
                        "Status: Active"
                    } else {
                        "Status: Previous Renter"
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (currentRenter.active) {

                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit")
                        }

                    } else {

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedButton(
                        onClick = onLedgerClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Ledger")
                    }
                }

                if (currentRenter.active) {

                    Button(
                        onClick = {
                            showEndTenancyDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("End Tenancy")
                    }
                }
            }

            if (showEndTenancyDialog) {

                EndTenancyDialog(
                    onDismiss = {
                        showEndTenancyDialog = false
                    },
                    onConfirm = { moveOutDate ->

                        renterViewModel.endTenancy(
                            renter = currentRenter,
                            moveOutDate = moveOutDate,
                            onSuccess = {
                                showEndTenancyDialog = false
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EndTenancyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    val context = LocalContext.current

    var moveOutDate by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("End Tenancy")
        },
        text = {

            Column {

                Text(
                    "Select the move-out date."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = {

                        val calendar = Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->

                                moveOutDate =
                                    "%02d/%02d/%04d".format(
                                        day,
                                        month + 1,
                                        year
                                    )

                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        if (moveOutDate.isEmpty()) {
                            "Select Move-out Date"
                        } else {
                            moveOutDate
                        }
                    )
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    if (moveOutDate.isNotEmpty()) {
                        onConfirm(moveOutDate)
                    }
                }
            ) {
                Text("End Tenancy")
            }
        },
        dismissButton = {

            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}