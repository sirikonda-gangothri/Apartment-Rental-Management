package com.example.apartmentrentalmanagement.screens.building

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Property(
    val name: String,
    val type: String,
    val propertyId: String,
    val location: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingListScreen(
    onBackClick: () -> Unit,
    onAddPropertyClick: () -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedStatus by remember {
        mutableStateOf("All Status")
    }

    var selectedType by remember {
        mutableStateOf("All Types")
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    var typeMenuExpanded by remember {
        mutableStateOf(false)
    }

    val properties = listOf(
        Property(
            name = "Flower Garden",
            type = "Hostel",
            propertyId = "HG-0131",
            location = "Hyderabad",
            status = "Active"
        ),
        Property(
            name = "Amala Palace",
            type = "Shortlet",
            propertyId = "HG-0231",
            location = "Hyderabad",
            status = "Active"
        ),
        Property(
            name = "Green Valley Apartments",
            type = "Apartment",
            propertyId = "AP-0145",
            location = "Hyderabad",
            status = "Active"
        ),
        Property(
            name = "Sunrise Residency",
            type = "Apartment",
            propertyId = "AP-0188",
            location = "Secunderabad",
            status = "Inactive"
        )
    )

    val filteredProperties = properties.filter { property ->

        val matchesSearch =
            property.name.contains(searchQuery, ignoreCase = true) ||
            property.propertyId.contains(searchQuery, ignoreCase = true) ||
            property.type.contains(searchQuery, ignoreCase = true)

        val matchesStatus =
            selectedStatus == "All Status" ||
                    property.status == selectedStatus

        val matchesType =
            selectedType == "All Types" ||
                    property.type == selectedType

        matchesSearch && matchesStatus && matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = "List of Properties",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add New button
        Button(
            onClick = onAddPropertyClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Add New Property"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search Properties")
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

        Spacer(modifier = Modifier.height(12.dp))

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Status filter
            Box(
                modifier = Modifier.weight(1f)
            ) {

                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Status")
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                ) {
                    TextButton(
                        onClick = {
                            statusMenuExpanded = true
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }

                DropdownMenu(
                    expanded = statusMenuExpanded,
                    onDismissRequest = {
                        statusMenuExpanded = false
                    }
                ) {

                    listOf(
                        "All Status",
                        "Active",
                        "Inactive"
                    ).forEach { status ->

                        DropdownMenuItem(
                            text = {
                                Text(status)
                            },
                            onClick = {
                                selectedStatus = status
                                statusMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Type filter
            Box(
                modifier = Modifier.weight(1f)
            ) {

                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("By Type")
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                ) {
                    TextButton(
                        onClick = {
                            typeMenuExpanded = true
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }

                DropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = {
                        typeMenuExpanded = false
                    }
                ) {

                    listOf(
                        "All Types",
                        "Apartment",
                        "Hostel",
                        "Shortlet"
                    ).forEach { type ->

                        DropdownMenuItem(
                            text = {
                                Text(type)
                            },
                            onClick = {
                                selectedType = type
                                typeMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${filteredProperties.size} Properties",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Property list
        if (filteredProperties.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No properties found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(filteredProperties) { property ->

                    PropertyCard(
                        property = property
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Property
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${property.type}: ${property.propertyId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = property.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
