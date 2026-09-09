package com.example.apartmentrentalmanagement.screens.building.addproperty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPropertyScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit = {}
) {

    var propertyName by remember {
        mutableStateOf("")
    }

    var propertyType by remember {
        mutableStateOf("")
    }

    var propertyId by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var floors by remember {
        mutableStateOf("")
    }

    var units by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("Active")
    }

    var typeMenuExpanded by remember {
        mutableStateOf(false)
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    var showError by remember {
        mutableStateOf(false)
    }

    val propertyTypes = listOf(
        "Apartment",
        "Hostel",
        "Shortlet"
    )

    val statusOptions = listOf(
        "Active",
        "Inactive"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Add New Property",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Section title
        Text(
            text = "Property Details",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Property name
        OutlinedTextField(
            value = propertyName,
            onValueChange = {
                propertyName = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Property Name")
            },
            placeholder = {
                Text("Enter property name")
            },
            singleLine = true,
            isError = showError && propertyName.isBlank()
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Property type
        Text(
            text = "Property Type",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = propertyType,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Select property type")
                },
                readOnly = true,
                singleLine = true,
                isError = showError && propertyType.isBlank()
            )

            DropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = {
                    typeMenuExpanded = false
                }
            ) {

                propertyTypes.forEach { type ->

                    DropdownMenuItem(
                        text = {
                            Text(type)
                        },
                        onClick = {
                            propertyType = type
                            typeMenuExpanded = false
                            showError = false
                        }
                    )
                }
            }

            Button(
                onClick = {
                    typeMenuExpanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (propertyType.isBlank()) {
                        "Select Type"
                    } else {
                        propertyType
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Property ID
        OutlinedTextField(
            value = propertyId,
            onValueChange = {
                propertyId = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Property ID")
            },
            placeholder = {
                Text("Example: AP-0131")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Location")
            },
            placeholder = {
                Text("Enter property location")
            },
            singleLine = true,
            isError = showError && location.isBlank()
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Floors and units
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = floors,
                onValueChange = {
                    if (it.all { character -> character.isDigit() }) {
                        floors = it
                    }
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Floors")
                },
                placeholder = {
                    Text("12")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = units,
                onValueChange = {
                    if (it.all { character -> character.isDigit() }) {
                        units = it
                    }
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Units")
                },
                placeholder = {
                    Text("48")
                },
                singleLine = true
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Status
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = status,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true
        )

        DropdownMenu(
            expanded = statusMenuExpanded,
            onDismissRequest = {
                statusMenuExpanded = false
            }
        ) {

            statusOptions.forEach { selectedStatus ->

                DropdownMenuItem(
                    text = {
                        Text(selectedStatus)
                    },
                    onClick = {
                        status = selectedStatus
                        statusMenuExpanded = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Save button
        Button(
            onClick = {

                if (
                    propertyName.isBlank() ||
                    propertyType.isBlank() ||
                    location.isBlank()
                ) {
                    showError = true
                } else {
                    onSaveClick()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Property"
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}
