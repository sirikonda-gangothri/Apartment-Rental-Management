package com.example.apartmentrentalmanagement.screens.building

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme

class PropertyManagementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApartmentRentalManagementTheme {
                PropertyManagementNavigation()
            }
        }
    }
}

@Composable
fun PropertyManagementNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "building_list"
    ) {
        composable("building_list") {
            BuildingListScreen()
        }
    }
}