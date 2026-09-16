package com.example.apartmentrentalmanagement.screens.rent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme

class RentManagementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApartmentRentalManagementTheme {
                RentManagementScreen()
            }
        }
    }
}

@Composable
fun RentManagementScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "rent_tracker"
    ) {

        composable("rent_tracker") {

            RentTrackerScreen(

                onBackClick = {
                    // handle this later
                },

                onRecordPaymentClick = {
                    // payment screen later
                },

                onLedgerClick = { renterId ->

                    // ledger later
                }
            )
        }
    }
}