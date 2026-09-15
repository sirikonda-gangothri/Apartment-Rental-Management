package com.example.apartmentrentalmanagement.screens.renter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterHistoryScreen(
    onBackClick: () -> Unit,
    onRenterClick: (String) -> Unit,
    renterViewModel: RenterViewModel = viewModel()
) {

    val renters by renterViewModel.renters.collectAsState()

    val previousRenters =
        renters.filter { !it.active }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Renter History")
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

        if (previousRenters.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                Text(
                    "No previous renters"
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    previousRenters,
                    key = { it.id }
                ) { renter ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {

                            Text(
                                text = renter.name,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Building: ${renter.propertyName}"
                            )

                            Text(
                                text = "Flat: ${renter.flatNumber}"
                            )

                            Text(
                                text = "Move-in: ${renter.leaseStartDate}"
                            )

                            Text(
                                text = "Move-out: ${renter.moveOutDate}"
                            )

                            Text(
                                text = "Monthly Rent: ₹${renter.monthlyRent}"
                            )

                            Text(
                                text = "Advance: ₹${renter.advanceAmount}"
                            )

                            androidx.compose.material3.TextButton(
                                onClick = {
                                    onRenterClick(renter.id)
                                }
                            ) {

                                Text("View Details")
                            }
                        }
                    }
                }
            }
        }
    }
}