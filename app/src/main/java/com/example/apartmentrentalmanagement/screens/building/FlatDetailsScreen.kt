package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.apartmentrentalmanagement.screens.renter.RenterManagementActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apartmentrentalmanagement.screens.renter.RenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatDetailsScreen(
    propertyDocumentId: String,
    flatId: String,
    onBackClick: () -> Unit,
    onEditFlatClick: () -> Unit
) {

    val buildingViewModel: BuildingViewModel = viewModel()

    val renterViewModel: RenterViewModel = viewModel()

    var flat by remember {
        mutableStateOf<Flat?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val renter by renterViewModel.selectedRenter.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(propertyDocumentId, flatId) {

        isLoading = true

        buildingViewModel.loadFlat(
            propertyDocumentId = propertyDocumentId,
            flatId = flatId
        ) { loadedFlat ->

            flat = loadedFlat
            isLoading = false

            // Load renter only when the flat has a renter
            loadedFlat.currentRenterId?.let { renterId ->

                renterViewModel.getRenter(renterId)
            }
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                title = {

                    Text(
                        text = "Flat Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { paddingValues ->

        if (isLoading) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {

                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            return@Scaffold
        }

        if (flat == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Flat not found",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onBackClick
                ) {
                    Text("Go Back")
                }
            }

            return@Scaffold
        }

        val currentFlat = flat!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Flat ${currentFlat.flatNumber}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = currentFlat.flatType,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================================
            // FLAT INFORMATION
            // =====================================================

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    DetailRow(
                        label = "Flat Number",
                        value = currentFlat.flatNumber
                    )

                    DetailRow(
                        label = "Floor",
                        value = currentFlat.floor.toString()
                    )

                    DetailRow(
                        label = "Flat Type",
                        value = currentFlat.flatType
                    )

                    DetailRow(
                        label = "Monthly Rent",
                        value = "₹${currentFlat.monthlyRent.toInt()}"
                    )

                    DetailRow(
                        label = "Status",
                        value = currentFlat.occupancyStatus
                            .replace("_", " ")   //"NOT_OCCUPIED" → "NOT OCCUPIED"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================================
            // CURRENT RENTER
            // =====================================================

            Text(
                text = "Current Renter",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (
                currentFlat.occupancyStatus.equals(
                    "OCCUPIED",
                    ignoreCase = true
                ) &&
                !currentFlat.currentRenterId.isNullOrBlank()
            ) {

                if (renter == null) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            CircularProgressIndicator()

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Loading renter..."
                            )
                        }
                    }

                } else {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                currentFlat.currentRenterId?.let { renterId ->

                                    val intent =
                                        Intent(
                                            context,
                                            RenterManagementActivity::class.java
                                        ).apply {
                                            putExtra(
                                                "renterId",
                                                renterId
                                            )
                                        }

                                    context.startActivity(intent)
                                }
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = renter?.name ?: "Unknown renter",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = renter?.phone ?: "No phone number",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = renter?.email
                                    ?: "No email address",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Monthly Rent: ₹${renter?.monthlyRent?.toInt() ?: 0}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

            } else {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "No current renter",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "This flat is currently vacant.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =====================================================
            // EDIT FLAT
            // =====================================================

            Button(
                onClick = onEditFlatClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Edit Flat")
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
