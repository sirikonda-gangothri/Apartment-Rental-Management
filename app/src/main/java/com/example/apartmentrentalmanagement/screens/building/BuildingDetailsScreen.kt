package com.example.apartmentrentalmanagement.screens.building

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun BuildingDetailsScreen(
    propertyDocumentId: String,
    buildingName: String,
    address: String,
    numberOfFloors: Int,
    onAddFlatClick: () -> Unit,
    onBulkGenerateClick: () -> Unit,
    onFlatClick: (Flat) -> Unit
) {

    val buildingViewModel: BuildingViewModel = viewModel()

    LaunchedEffect(propertyDocumentId) {
        buildingViewModel.loadProperty(propertyDocumentId)
        buildingViewModel.loadFlats(propertyDocumentId)
    }

    val property = buildingViewModel.property
    val flats = buildingViewModel.flats

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }


    val filteredFlats = flats.filter { flat ->

        val matchesSearch =
            flat.flatNumber.contains(
                searchText,
                ignoreCase = true
            )

        val matchesFilter =
            when (selectedFilter) {
                "Occupied" -> flat.occupancyStatus == "OCCUPIED"
                "Vacant" -> flat.occupancyStatus == "VACANT"
                else -> true
            }

        matchesSearch && matchesFilter
    }


    val occupiedCount = flats.count {
        it.occupancyStatus == "OCCUPIED"
    }

    val vacantCount = flats.count {
        it.occupancyStatus == "VACANT"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = buildingName,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = address,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "${property?.floors ?: numberOfFloors} Floors",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Card(
                modifier = Modifier.weight(1f)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "Total Flats",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = flats.size.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }


            Card(
                modifier = Modifier.weight(1f)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "Occupied",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = occupiedCount.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }


            Card(
                modifier = Modifier.weight(1f)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "Vacant",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = vacantCount.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Button(
            onClick = onAddFlatClick,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Add Flat"
            )
        }

        Button(
            onClick = onBulkGenerateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Flats in Bulk")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search Flat")
            },
            singleLine = true
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = selectedFilter == "All",
                onClick = {
                    selectedFilter = "All"
                },
                label = {
                    Text("All")
                }
            )

            FilterChip(
                selected = selectedFilter == "Occupied",
                onClick = {
                    selectedFilter = "Occupied"
                },
                label = {
                    Text("Occupied")
                }
            )

            FilterChip(
                selected = selectedFilter == "Vacant",
                onClick = {
                    selectedFilter = "Vacant"
                },
                label = {
                    Text("Vacant")
                }
            )
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = filteredFlats,
                key = { flat ->
                    flat.id
                }
            ) { flat ->

                FlatCard(
                    flat = flat,
                    onClick = {
                        onFlatClick(flat)
                    }
                )
            }
        }
    }
}


@Composable
fun FlatCard(
    flat: Flat,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Flat ${flat.flatNumber}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Floor: ${flat.floor}"
            )

            Text(
                text = "Type: ${flat.flatType}"
            )

            Text(
                text = "Monthly Rent: ₹${flat.monthlyRent}"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Status: ${flat.occupancyStatus}"
            )
        }
    }
}