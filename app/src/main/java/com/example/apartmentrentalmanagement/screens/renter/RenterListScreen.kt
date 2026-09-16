package com.example.apartmentrentalmanagement.screens.renter

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@androidx.compose.runtime.Composable
fun RenterListScreen(
    onBackClick: () -> Unit,
    onAddRenterClick: () -> Unit,
    onRenterClick: (String) -> Unit,
    onHistoryClick: () -> Unit,
    renterViewModel: RenterViewModel = viewModel()
) {

    val renters by renterViewModel.renters.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }

    var showActiveOnly by remember {
        mutableStateOf(true)
    }

    val filteredRenters = renters.filter { renter ->

        val matchesSearch =
            renter.name.contains(searchText, ignoreCase = true) ||
                    renter.phone.contains(searchText) ||
                    renter.propertyName.contains(searchText, ignoreCase = true) ||
                    renter.flatNumber.contains(searchText, ignoreCase = true)

        val matchesFilter =
            if (showActiveOnly) renter.active else !renter.active

        matchesSearch && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Renters",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onHistoryClick
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Renter History"
                    )
                }

                IconButton(
                    onClick = {
                        showActiveOnly = !showActiveOnly
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter"
                    )
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Search renters")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = if (showActiveOnly) {
                    "Current Renters"
                } else {
                    "Previous Renters"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (filteredRenters.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (searchText.isEmpty()) {
                            "No renters found"
                        } else {
                            "No matching renters"
                        }
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = filteredRenters,
                        key = { it.id }
                    ) { renter ->

                        RenterCard(
                            renter = renter,
                            onClick = {
                                onRenterClick(renter.id)
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddRenterClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Renter"
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun RenterCard(
    renter: Renter,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Renter name + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = renter.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (renter.active) {
                        "Active"
                    } else {
                        "Inactive"
                    },
                    color = if (renter.active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Building + Flat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "Building",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = renter.propertyName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Flat",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = renter.flatNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Phone
            Text(
                text = "Phone",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = renter.phone,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Monthly rent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Monthly Rent",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "₹${renter.monthlyRent}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
