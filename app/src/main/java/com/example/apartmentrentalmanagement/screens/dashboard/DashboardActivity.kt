package com.example.apartmentrentalmanagement.screens.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale
import com.example.apartmentrentalmanagement.screens.auth.LoginActivity
import com.example.apartmentrentalmanagement.screens.profile.ProfileActivity
import com.example.apartmentrentalmanagement.screens.building.PropertyManagementActivity
import com.example.apartmentrentalmanagement.screens.rent.RentManagementActivity
import com.example.apartmentrentalmanagement.screens.renter.RenterManagementActivity
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme
import com.example.apartmentrentalmanagement.screens.dashboard.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApartmentRentalManagementTheme {
                DashboardScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DashboardScreen() {

        var selectedItem by remember {
            mutableIntStateOf(0)
        }

        val dashboardViewModel: DashboardViewModel = viewModel()

        val analytics = dashboardViewModel.analytics

        val isLoading = dashboardViewModel.isLoading

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Nivasa",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },

                    actions = {

                        IconButton(
                            onClick = {

                                startActivity(
                                    Intent(
                                        this@DashboardActivity,
                                        ProfileActivity::class.java
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }

                        IconButton(
                            onClick = {
                                logout()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout"
                            )
                        }
                    }
                )
            },

            bottomBar = {

                NavigationBar {

                    NavigationBarItem(
                        selected = selectedItem == 0,
                        onClick = {
                            startActivity(
                                Intent(
                                    this@DashboardActivity,
                                    PropertyManagementActivity::class.java
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = "Manage Apartments"
                            )
                        },
                        label = {
                            Text("Apartments")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 1,
                        onClick = {
                            startActivity(
                                Intent(
                                    this@DashboardActivity,
                                    RenterManagementActivity::class.java
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Manage Renters"
                            )
                        },
                        label = {
                            Text("Renters")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 2,
                        onClick = {
                                startActivity(
                                    Intent(
                                        this@DashboardActivity,
                                        RentManagementActivity::class.java
                                    )
                                )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Rent Tracker"
                            )
                        },
                        label = {
                            Text("Rent Tracker")
                        }
                    )
                }
            }

        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "Welcome!👋",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    DashboardCard(
                        title = "Total Flats",
                        value = if (isLoading) {
                            "--"
                        } else {
                            analytics.totalFlats.toString()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DashboardCard(
                        title = "Occupied",
                        value = if (isLoading) {
                            "--"
                        } else {
                            analytics.occupiedFlats.toString()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DashboardCard(
                        title = "Vacant",
                        value = if (isLoading) {
                            "--"
                        } else {
                            analytics.vacantFlats.toString()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    DashboardCard(
                        title = "Expected Rent",
                        value = if (isLoading) {
                            "₹--"
                        } else {
                            formatRupees(analytics.expectedRent)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DashboardCard(
                        title = "Collected Rent",
                        value = if (isLoading) {
                            "₹--"
                        } else {
                            formatRupees(analytics.collectedRent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                DashboardCard(
                    title = "Pending Rent",
                    value = if (isLoading) {
                        "₹--"
                    } else {
                        formatRupees(analytics.pendingRent)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    if (isLoading) {

                        Text(
                            text = "Loading analytics..."
                        )

                    } else {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Occupancy Rate: " +
                                        "${analytics.occupancyPercentage.toInt()}%"
                            )

                            Text(
                                text = "Rent Collection: " +
                                        "${analytics.collectionPercentage.toInt()}%"
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DashboardCard(
        title: String,
        value: String,
        modifier: Modifier = Modifier
    ) {

        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    private fun logout() {

        FirebaseAuth
            .getInstance()
            .signOut()

        val intent = Intent(
            this,
            LoginActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
    }
}

private fun formatRupees(amount: Double): String {

    val formatter =
        NumberFormat.getNumberInstance(
            Locale("en", "IN")
        )

    formatter.maximumFractionDigits = 0

    return "₹${formatter.format(amount)}"
}