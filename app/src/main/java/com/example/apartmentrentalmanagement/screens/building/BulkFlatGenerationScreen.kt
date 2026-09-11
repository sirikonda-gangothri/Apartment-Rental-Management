package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

data class BulkFloor(
    val floorNumber: Int,
    val flatCount: String = "",
    val flatNumbers: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkFlatGenerationScreen(
    propertyDocumentId: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {

    val buildingViewModel: BuildingViewModel = viewModel()

    LaunchedEffect(propertyDocumentId) {
        buildingViewModel.loadProperty(propertyDocumentId)
    }

    val property = buildingViewModel.property

    val floors = remember(property?.floors) {
        mutableStateListOf<BulkFloor>().apply {

            val floorCount = property?.floors ?: 0

            repeat(floorCount) { index ->

                add(
                    BulkFloor(
                        floorNumber = index + 1
                    )
                )
            }
        }
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
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

                    Column {

                        Text(
                            text = property?.name ?: "Bulk Add Flats",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${property?.floors ?: 0} Floors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Add Flats Floor by Floor",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Enter the number of flats and their actual flat numbers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LazyColumn(

                modifier = Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {

                items(
                    items = floors,
                    key = { it.floorNumber }
                ) { floor ->

                    FloorBulkCard(

                        floor = floor,

                        onCountChange = { newCount ->

                            val count =
                                newCount.toIntOrNull() ?: 0

                            val updatedNumbers =
                                List(count) { index ->

                                    floor.flatNumbers
                                        .getOrNull(index)
                                        ?: ""
                                }

                            val index =
                                floors.indexOfFirst {
                                    it.floorNumber ==
                                            floor.floorNumber
                                }

                            if (index != -1) {

                                floors[index] =
                                    floor.copy(
                                        flatCount = newCount,
                                        flatNumbers =
                                            updatedNumbers
                                    )
                            }
                        },

                        onFlatNumberChange = {
                                numberIndex,
                                value ->

                            val updatedNumbers =
                                floor.flatNumbers.toMutableList()

                            updatedNumbers[numberIndex] =
                                value

                            val index =
                                floors.indexOfFirst {
                                    it.floorNumber ==
                                            floor.floorNumber
                                }

                            if (index != -1) {

                                floors[index] =
                                    floor.copy(
                                        flatNumbers =
                                            updatedNumbers
                                    )
                            }
                        }
                    )
                }
            }

            errorMessage?.let { message ->

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(

                onClick = {

                    errorMessage = null

                    val allFlatNumbers =
                        floors
                            .flatMap {
                                it.flatNumbers
                            }
                            .map {
                                it.trim()
                            }

                    if (allFlatNumbers.isEmpty()) {

                        errorMessage =
                            "Please add at least one flat."

                        return@Button
                    }

                    if (allFlatNumbers.any { it.isBlank() }) {

                        errorMessage =
                            "Please enter every flat number."

                        return@Button
                    }

                    val duplicateNumbers =
                        allFlatNumbers
                            .groupingBy { it }
                            .eachCount()
                            .filterValues {
                                it > 1
                            }
                            .keys

                    if (duplicateNumbers.isNotEmpty()) {

                        errorMessage =
                            "Duplicate flat numbers: ${
                                duplicateNumbers.joinToString(", ")
                            }"

                        return@Button
                    }

                    val generatedFlats =
                        floors.flatMap { floor ->

                            floor.flatNumbers.map { flatNumber ->

                                Flat(

                                    flatNumber =
                                        flatNumber.trim(),

                                    floor =
                                        floor.floorNumber,

                                    flatType = "",

                                    monthlyRent = 0.0,

                                    occupancyStatus =
                                        "VACANT",

                                    currentRenterId = null
                                )
                            }
                        }

                    buildingViewModel.generateFlats(

                        propertyDocumentId =
                            propertyDocumentId,

                        flats =
                            generatedFlats,

                        onSuccess =
                            onSaveSuccess,

                        onFailure = { message ->

                            errorMessage = message
                        }
                    )
                },

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    !buildingViewModel.isSaving

            ) {

                if (buildingViewModel.isSaving) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(20.dp)
                    )

                } else {

                    Text("Generate Flats")
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun FloorBulkCard(
    floor: BulkFloor,
    onCountChange: (String) -> Unit,
    onFlatNumberChange: (Int, String) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Floor ${floor.floorNumber}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${floor.flatNumbers.size} Flats",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(

                value = floor.flatCount,

                onValueChange =
                    onCountChange,

                label = {
                    Text("Number of flats")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            if (floor.flatNumbers.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Flat Numbers",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                floor.flatNumbers.forEachIndexed {
                        index,
                        flatNumber ->

                    OutlinedTextField(

                        value = flatNumber,

                        onValueChange = {
                            onFlatNumberChange(
                                index,
                                it
                            )
                        },

                        label = {
                            Text(
                                "Flat ${index + 1}"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }
    }
}