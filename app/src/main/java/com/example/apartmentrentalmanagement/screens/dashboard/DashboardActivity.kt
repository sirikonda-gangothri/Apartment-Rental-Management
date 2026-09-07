package com.example.apartmentrentalmanagement.screens.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apartmentrentalmanagement.screens.profile.ProfileActivity
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApartmentRentalManagementTheme {
                DashboardScreen()
            }
        }
    }

    @Composable
    fun DashboardScreen() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Welcome to Nivasa",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Apartment Rental Management",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Apartments feature will be added next
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Apartments")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Renters feature will be added next
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Renters")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Rent Tracker feature will be added next
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rent Tracker")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Profile feature will be added next
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            ProfileActivity::class.java
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Profile")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Logout will be added next
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

