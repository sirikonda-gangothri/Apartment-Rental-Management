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
import androidx.compose.ui.draw.clip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Property(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val propertyId: String = "",
    val location: String = "",
    val status: String = "",
    val floors: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingListScreen(
    onBackClick: () -> Unit,
    onAddPropertyClick: () -> Unit,
    onPropertyClick: (Property) -> Unit
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

    val properties = remember {
        mutableStateListOf<Property>()
    }

    LaunchedEffect(Unit) {

        val currentUser = FirebaseAuth
            .getInstance()
            .currentUser

        if (currentUser != null) {

            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(currentUser.uid)
                .collection("properties")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        println("Firestore error: ${error.message}")
                        return@addSnapshotListener
                    }

                    properties.clear()

                    snapshot?.documents?.forEach { document ->

                        val property = Property(
                            id = document.id,
                            name = document.getString("propertyName") ?: "",
                            type = document.getString("propertyType") ?: "",
                            propertyId = document.getString("propertyId") ?: "",
                            location = document.getString("location") ?: "",
                            status = document.getString("status") ?: "",
                            floors = document.getLong("floors")?.toInt() ?: 0
                        )

                        properties.add(property)
                    }
                }
        }
    }

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
                text = "Manage Properties",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Add New Property button
        Button(
            onClick = onAddPropertyClick,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Property",
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = "Add New Property"
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search properties"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Status filter
            Box(
                modifier = Modifier.weight(1f)
            ) {

                Button(
                    onClick = {
                        statusMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedStatus
                    )
                }

                DropdownMenu(
                    expanded = statusMenuExpanded,
                    onDismissRequest = {
                        statusMenuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("All Status")
                        },
                        onClick = {
                            selectedStatus = "All Status"
                            statusMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Active")
                        },
                        onClick = {
                            selectedStatus = "Active"
                            statusMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Inactive")
                        },
                        onClick = {
                            selectedStatus = "Inactive"
                            statusMenuExpanded = false
                        }
                    )
                }
            }

            // Type filter
            Box(
                modifier = Modifier.weight(1f)
            ) {

                Button(
                    onClick = {
                        typeMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedType
                    )
                }

                DropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = {
                        typeMenuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("All Types")
                        },
                        onClick = {
                            selectedType = "All Types"
                            typeMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Apartment")
                        },
                        onClick = {
                            selectedType = "Apartment"
                            typeMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Hostel")
                        },
                        onClick = {
                            selectedType = "Hostel"
                            typeMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Shortlet")
                        },
                        onClick = {
                            selectedType = "Shortlet"
                            typeMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "${filteredProperties.size} Properties",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (filteredProperties.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {

                Text(
                    text = "No properties found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(filteredProperties) { property ->

                    PropertyCard(
                        property = property,
                        onClick = {
                            onPropertyClick(property)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "${property.type}: ${property.propertyId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = property.status,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (property.status == "Active") {
                        Color(0xFF2E7D32)
                    } else {
                        Color(0xFFC62828)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (property.status == "Active") {
                                Color(0xFFE8F5E9)
                            } else {
                                Color(0xFFFFEBEE)
                            }
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.size(6.dp)
                )

                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}