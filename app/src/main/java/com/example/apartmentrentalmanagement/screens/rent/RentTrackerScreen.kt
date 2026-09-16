package com.example.apartmentrentalmanagement.screens.rent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentTrackerScreen(
    onBackClick: () -> Unit,
    onRecordPaymentClick: () -> Unit,
    onLedgerClick: (String) -> Unit
) {

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    val rentStatuses = listOf(
        RentStatus(
            renterId = "1",
            renterName = "Ravi Kumar",
            propertyId = "1",
            propertyName = "Green Residency",
            flatId = "101",
            flatNumber = "101",
            month = "September",
            year = 2026,
            expectedRent = 10000.0,
            paidAmount = 10000.0,
            pendingAmount = 0.0,
            paymentStatus = "PAID"
        ),

        RentStatus(
            renterId = "2",
            renterName = "Priya",
            propertyId = "1",
            propertyName = "Green Residency",
            flatId = "102",
            flatNumber = "102",
            month = "September",
            year = 2026,
            expectedRent = 12000.0,
            paidAmount = 8000.0,
            pendingAmount = 4000.0,
            paymentStatus = "PARTIAL"
        )
    )

    val expectedRent = rentStatuses.sumOf { it.expectedRent }
    val collectedRent = rentStatuses.sumOf { it.paidAmount }
    val pendingRent = rentStatuses.sumOf { it.pendingAmount }

    val filteredStatuses = rentStatuses.filter { rentStatus ->

        when (selectedFilter) {
            "Paid" -> rentStatus.paymentStatus == "PAID"
            "Partial" -> rentStatus.paymentStatus == "PARTIAL"
            "Pending" -> rentStatus.paymentStatus == "PENDING"
            else -> true
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Rent Tracker")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Month")

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text("September 2026")

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text("Building")

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text("All Buildings")

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Expected\n₹$expectedRent")

                Text("Collected\n₹$collectedRent")

                Text("Pending\n₹$pendingRent")
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "All",
                    "Paid",
                    "Partial",
                    "Pending"
                ).forEach { filter ->

                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                        },
                        label = {
                            Text(filter)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(filteredStatuses) { rentStatus ->

                    RentStatusCard(
                        rentStatus = rentStatus,
                        onClick = {
                            onLedgerClick(rentStatus.renterId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RentStatusCard(
    rentStatus: RentStatus,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = rentStatus.propertyName
            )

            Text(
                text = "Flat ${rentStatus.flatNumber}"
            )

            Text(
                text = rentStatus.renterName
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Expected: ₹${rentStatus.expectedRent}"
            )

            Text(
                text = "Paid: ₹${rentStatus.paidAmount}"
            )

            Text(
                text = "Pending: ₹${rentStatus.pendingAmount}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = rentStatus.paymentStatus
            )
        }
    }
}