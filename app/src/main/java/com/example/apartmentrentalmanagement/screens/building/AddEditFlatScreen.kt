package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddEditFlatScreen(
    buildingId: String,
    flat: Flat? = null,
    onSaveSuccess: () -> Unit
) {

    val buildingViewModel: BuildingViewModel = viewModel()

    var flatNumber by remember {
        mutableStateOf(flat?.flatNumber ?: "")
    }

    var floor by remember {
        mutableStateOf(
            flat?.floor?.toString() ?: ""
        )
    }

    var flatType by remember {
        mutableStateOf(flat?.flatType ?: "")
    }

    var monthlyRent by remember {
        mutableStateOf(
            if (flat != null) {
                flat.monthlyRent.toString()
            } else {
                ""
            }
        )
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = if (flat == null) {
                "Add Flat"
            } else {
                "Edit Flat"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = flatNumber,
            onValueChange = {
                flatNumber = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Flat Number")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = floor,
            onValueChange = {
                floor = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Floor")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = flatType,
            onValueChange = {
                flatType = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Flat Type")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = monthlyRent,
            onValueChange = {
                monthlyRent = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Monthly Rent")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = {

                if (
                    flatNumber.isBlank() ||
                    floor.isBlank() ||
                    flatType.isBlank() ||
                    monthlyRent.isBlank()
                ) {

                    errorMessage = "Please fill all fields"

                } else if (floor.toIntOrNull() == null) {

                    errorMessage = "Floor must be a number"

                } else if (monthlyRent.toDoubleOrNull() == null) {

                    errorMessage = "Monthly rent must be a valid number"

                } else {

                    errorMessage = ""

                    val newFlat = Flat(
                        id = flat?.id ?: "",
                        flatNumber = flatNumber,
                        floor = floor.toInt(),
                        flatType = flatType,
                        monthlyRent = monthlyRent.toDouble(),
                        occupancyStatus = flat?.occupancyStatus ?: "VACANT",
                        currentRenterId = flat?.currentRenterId
                    )

                    buildingViewModel.saveFlat(
                        propertyDocumentId = buildingId,
                        flat = newFlat,
                        onSuccess = onSaveSuccess
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (flat == null) {
                    "Save Flat"
                } else {
                    "Save Changes"
                }
            )
        }
    }
}