package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingDetailsScreen(
    propertyDocumentId: String,
    onBackClick: () -> Unit,
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

    var selectedFloor by remember {
        mutableStateOf<Int?>(null)
    }

    val floors = remember(flats, property?.floors) {
        flats
            .map { it.floor }
            .distinct()
            .sorted()
    }

    val filteredFlats = flats
        .filter { flat ->

            val matchesSearch =
                flat.flatNumber.contains(
                    searchText,
                    ignoreCase = true
                )

            val matchesStatus =
                when (selectedFilter) {
                    "Occupied" ->
                        flat.occupancyStatus == "OCCUPIED"

                    "Vacant" ->
                        flat.occupancyStatus == "VACANT"

                    else -> true
                }

            val matchesFloor =
                selectedFloor == null ||
                        flat.floor == selectedFloor

            matchesSearch &&
                    matchesStatus &&
                    matchesFloor
        }
        .sortedWith(
            compareBy<Flat> { it.floor }
                .thenBy { flat ->
                    flat.flatNumber.toIntOrNull()
                        ?: Int.MAX_VALUE
                }
        )

    val occupiedCount = flats.count {
        it.occupancyStatus == "OCCUPIED"
    }

    val vacantCount = flats.count {
        it.occupancyStatus == "VACANT"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text("Building Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = property?.name ?: "Loading...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = property?.location ?: "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "${property?.floors ?: 0} Floors • ${flats.size} Flats",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                SummaryCard(
                    title = "Total",
                    value = flats.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Occupied",
                    value = occupiedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Vacant",
                    value = vacantCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onAddFlatClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Flat")
                }

                Button(
                    onClick = onBulkGenerateClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Bulk Generate")
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
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

            Text(
                text = "Floor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = selectedFloor == null,
                    onClick = {
                        selectedFloor = null
                    },
                    label = {
                        Text("All Floors")
                    }
                )

                floors.forEach { floor ->

                    FilterChip(
                        selected = selectedFloor == floor,
                        onClick = {
                            selectedFloor = floor
                        },
                        label = {
                            Text("Floor $floor")
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (selectedFloor == null) {
                        "All Flats"
                    } else {
                        "Floor $selectedFloor"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "${filteredFlats.size} flats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    count = filteredFlats.size,
                    key = { index ->
                        filteredFlats[index].id
                    }
                ) { index ->

                    val flat = filteredFlats[index]

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
}


@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
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
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = flat.flatNumber,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = flat.flatType,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "₹${flat.monthlyRent.toInt()} / month",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = flat.occupancyStatus.replace(
                    "_",
                    " "
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}