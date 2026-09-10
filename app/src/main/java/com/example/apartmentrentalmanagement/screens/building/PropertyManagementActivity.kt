package com.example.apartmentrentalmanagement.screens.building

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apartmentrentalmanagement.screens.building.addproperty.AddPropertyScreen
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

        // --------------------------------------------------
        // BUILDING LIST
        // --------------------------------------------------

        composable("building_list") {

            BuildingListScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onAddPropertyClick = {
                    navController.navigate("add_property")
                },

                onPropertyClick = { property ->

                    navController.navigate(
                        "building_details/${property.id}"
                    )
                }
            )
        }


        // --------------------------------------------------
        // ADD PROPERTY
        // --------------------------------------------------

        composable("add_property") {

            AddPropertyScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }


        // --------------------------------------------------
        // BUILDING DETAILS
        // --------------------------------------------------

        composable(
            route = "building_details/{propertyDocumentId}",
            arguments = listOf(
                navArgument("propertyDocumentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val propertyDocumentId =
                backStackEntry.arguments?.getString(
                    "propertyDocumentId"
                ) ?: return@composable

            BuildingDetailsScreen(

                propertyDocumentId = propertyDocumentId,

                buildingName = "Building",

                address = "",

                numberOfFloors = 0,

                onAddFlatClick = {

                    navController.navigate(
                        "add_edit_flat/$propertyDocumentId"
                    )
                },

                onBulkGenerateClick = {

                    navController.navigate(
                        "bulk_generate_flats/$propertyDocumentId"
                    )
                },

                onFlatClick = { flat ->

                    navController.navigate(
                        "add_edit_flat/$propertyDocumentId?flatId=${flat.id}"
                    )
                }
            )
        }


        // --------------------------------------------------
        // BULK FLAT GENERATION
        // --------------------------------------------------

        composable(
            route = "bulk_generate_flats/{propertyDocumentId}",
            arguments = listOf(
                navArgument("propertyDocumentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val propertyDocumentId =
                backStackEntry.arguments?.getString(
                    "propertyDocumentId"
                ) ?: return@composable

            BulkFlatGenerationScreen(

                propertyDocumentId = propertyDocumentId,

                numberOfFloors = 5,

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }


        // --------------------------------------------------
        // ADD / EDIT FLAT
        // --------------------------------------------------

        composable(
            route = "add_edit_flat/{propertyDocumentId}?flatId={flatId}",
            arguments = listOf(

                navArgument("propertyDocumentId") {
                    type = NavType.StringType
                },

                navArgument("flatId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->

            val propertyDocumentId =
                backStackEntry.arguments?.getString(
                    "propertyDocumentId"
                ) ?: return@composable

            val flatId =
                backStackEntry.arguments?.getString("flatId")

            AddEditFlatScreen(

                buildingId = propertyDocumentId,

                flat = null,

                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}