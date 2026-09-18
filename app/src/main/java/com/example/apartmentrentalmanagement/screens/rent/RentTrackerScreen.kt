package com.example.apartmentrentalmanagement.screens.rent

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
//import androidx.compose.foundation.lazy.stickyHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentTrackerScreen(
    viewModel: RentViewModel,
    onRenterClick: (String) -> Unit,
    onBackClick: () -> Unit
) {

    // =========================================================
    // VIEWMODEL STATE
    // =========================================================

    val rentStatuses by
    viewModel.rentStatuses.collectAsState()

    val selectedMonth by
    viewModel.selectedMonth.collectAsState()

    val selectedYear by
    viewModel.selectedYear.collectAsState()

    val totalExpected by
    viewModel.totalExpected.collectAsState()

    val totalCollected by
    viewModel.totalCollected.collectAsState()

    val totalPending by
    viewModel.totalPending.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val errorMessage by
    viewModel.errorMessage.collectAsState()


    // =========================================================
    // LOCAL UI STATE
    // =========================================================

    var searchText by
    remember {
        mutableStateOf("")
    }

    var selectedBuilding by
    remember {
        mutableStateOf("All Buildings")
    }

    var selectedStatus by
    remember {
        mutableStateOf("ALL")
    }


    // =========================================================
    // BUILDING LIST
    // =========================================================

    val buildings =
        remember(rentStatuses) {

            listOf("All Buildings") +
                    rentStatuses
                        .map {
                            it.propertyName
                        }
                        .filter {
                            it.isNotBlank()
                        }
                        .distinct()
                        .sorted()
        }


    // =========================================================
    // FILTER DATA
    // =========================================================

    val filteredStatuses =
        remember(
            rentStatuses,
            selectedBuilding,
            selectedStatus,
            searchText
        ) {

            rentStatuses
                .filter { rentStatus ->

                    // -----------------------------------------
                    // Building filter
                    // -----------------------------------------

                    selectedBuilding ==
                            "All Buildings" ||
                            rentStatus.propertyName ==
                            selectedBuilding
                }
                .filter { rentStatus ->

                    // -----------------------------------------
                    // Status filter
                    // -----------------------------------------

                    selectedStatus ==
                            "ALL" ||
                            rentStatus.paymentStatus ==
                            selectedStatus
                }
                .filter { rentStatus ->

                    // -----------------------------------------
                    // Search
                    // -----------------------------------------

                    val query =
                        searchText
                            .trim()
                            .lowercase()

                    if (query.isBlank()) {

                        true

                    } else {

                        rentStatus.renterName
                            .lowercase()
                            .contains(query) ||

                                rentStatus.flatNumber
                                    .lowercase()
                                    .contains(query)
                    }
                }
                .sortedWith(
                    compareBy(
                        { it.propertyName },
                        { flatNumberForSorting(it.flatNumber) }
                    )
                )
        }


    // =========================================================
    // GROUP BY BUILDING
    // =========================================================

    val groupedStatuses =
        filteredStatuses.groupBy {
            it.propertyName
        }


    // =========================================================
    // SCREEN
    // =========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(text="Rent Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
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

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                // =================================================
                // MONTH + BUILDING + SEARCH + SUMMARY
                // =================================================

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp
                            )
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        // -----------------------------------------
                        // Month
                        // -----------------------------------------

                        Text(
                            text =
                                "${getMonthName(selectedMonth)} $selectedYear",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        // -----------------------------------------
                        // Building filter
                        // -----------------------------------------

                        BuildingSelector(
                            buildings = buildings,

                            selectedBuilding =
                                selectedBuilding,

                            onBuildingSelected = {
                                selectedBuilding =
                                    it
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        // -----------------------------------------
                        // Search
                        // -----------------------------------------

                        OutlinedTextField(

                            value =
                                searchText,

                            onValueChange = {
                                searchText =
                                    it
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            singleLine = true,

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Default.Search,

                                    contentDescription =
                                        "Search"
                                )
                            },

                            placeholder = {

                                Text(
                                    "Search renter or flat"
                                )
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        // -----------------------------------------
                        // Summary
                        // -----------------------------------------

                        RentSummary(
                            totalExpected =
                                totalExpected,

                            totalCollected =
                                totalCollected,

                            totalPending =
                                totalPending
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        // -----------------------------------------
                        // Status filters
                        // -----------------------------------------

                        StatusFilters(
                            selectedStatus =
                                selectedStatus,

                            onStatusSelected = {
                                selectedStatus =
                                    it
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }
                }


                // =================================================
                // BUILDING GROUPS
                // =================================================

                groupedStatuses.forEach { (buildingName, statuses) ->

                    item(
                        key = "building_$buildingName"
                    ) {

                        BuildingHeader(
                            buildingName =
                                buildingName
                        )
                    }


                    items(
                        items = statuses,

                        key = {
                            it.renterId
                        }
                    ) { rentStatus ->

                        RentStatusRow(
                            rentStatus =
                                rentStatus,

                            onClick = {

                                onRenterClick(
                                    rentStatus.renterId
                                )
                            }
                        )
                    }
                }


                // =================================================
                // EMPTY RESULT
                // =================================================

                if (
                    groupedStatuses.isEmpty()
                ) {

                    item {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    if (
                                        errorMessage != null
                                    ) {
                                        errorMessage!!
                                    } else {
                                        "No renters found"
                                    },

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyLarge
                            )
                        }
                    }
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}


// =================================================================
// BUILDING SELECTOR
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildingSelector(
    buildings: List<String>,
    selectedBuilding: String,
    onBuildingSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Building",

            style = MaterialTheme
                .typography
                .labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,

            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            OutlinedTextField(
                value = selectedBuilding,

                onValueChange = {},

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),

                readOnly = true,

                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,

                onDismissRequest = {
                    expanded = false
                }
            ) {

                buildings.forEach { building ->

                    DropdownMenuItem(
                        text = {
                            Text(building)
                        },

                        onClick = {

                            onBuildingSelected(
                                building
                            )

                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


// =================================================================
// STATUS FILTERS
// =================================================================

@Composable
private fun StatusFilters(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        val statuses =
            listOf(
                "ALL" to "All",
                "PAID" to "Paid",
                "PARTIAL" to "Partial",
                "PENDING" to "Pending"
            )


        statuses.forEach { (value, label) ->

            FilterChip(

                selected =
                    selectedStatus ==
                            value,

                onClick = {

                    onStatusSelected(
                        value
                    )
                },

                label = {
                    Text(label)
                }
            )
        }
    }
}


// =================================================================
// SUMMARY
// =================================================================

@Composable
private fun RentSummary(
    totalExpected: Double,
    totalCollected: Double,
    totalPending: Double
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Rent Summary",

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            SummaryItem(
                title = "Expected",
                amount = totalExpected
            )

            SummaryItem(
                title = "Collected",
                amount = totalCollected
            )

            SummaryItem(
                title = "Pending",
                amount = totalPending
            )
        }
    }
}


// =================================================================
// SUMMARY ITEM
// =================================================================

@Composable
private fun SummaryItem(
    title: String,
    amount: Double
) {

    Column {

        Text(
            text = title,

            style =
                MaterialTheme
                    .typography
                    .labelMedium
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                formatCurrency(amount),

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )
    }
}


// =================================================================
// BUILDING HEADER
// =================================================================

@Composable
private fun BuildingHeader(
    buildingName: String
) {

    Text(
        text = buildingName,

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

        style =
            MaterialTheme
                .typography
                .titleMedium,

        fontWeight =
            FontWeight.Bold
    )
}


// =================================================================
// RENT STATUS ROW
// =================================================================

@Composable
private fun RentStatusRow(
    rentStatus: RentStatus,
    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
    ) {

        // ---------------------------------------------------------
        // Flat + renter
        // ---------------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text =
                        "Flat ${rentStatus.flatNumber}",

                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        rentStatus.renterName
                )
            }


            Text(
                text =
                    rentStatus.paymentStatus,

                style =
                    MaterialTheme
                        .typography
                        .labelMedium,

                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        // ---------------------------------------------------------
        // Amounts
        // ---------------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    "${formatCurrency(rentStatus.expectedRent)} expected"
            )

            Text(
                text =
                    "${formatCurrency(rentStatus.paidAmount)} paid"
            )
        }


        // ---------------------------------------------------------
        // Pending
        // ---------------------------------------------------------

        if (
            rentStatus.pendingAmount > 0
        ) {

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "${formatCurrency(rentStatus.pendingAmount)} pending"
            )
        }
    }
}


// =================================================================
// MONTH NAME
// =================================================================

private fun getMonthName(
    month: Int
): String {

    return when (month) {

        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"

        else -> ""
    }
}


// =================================================================
// CURRENCY FORMAT
// =================================================================

private fun formatCurrency(
    amount: Double
): String {

    val formatter =
        NumberFormat.getCurrencyInstance(
            Locale("en", "IN")
        )

    return formatter.format(amount)
}


// =================================================================
// FLAT NUMBER SORTING
// =================================================================

private fun flatNumberForSorting(
    flatNumber: String
): Int {

    return flatNumber
        .filter {
            it.isDigit()
        }
        .toIntOrNull()
        ?: Int.MAX_VALUE
}
