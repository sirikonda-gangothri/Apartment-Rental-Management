package com.example.apartmentrentalmanagement.screens.renter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apartmentrentalmanagement.screens.renter.addrenter.AddEditRenterScreen
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme

class RenterManagementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val renterId =
            intent.getStringExtra("renterId")

        setContent {
            ApartmentRentalManagementTheme() {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,

                    startDestination =
                        if (renterId != null) {
                            "renter_details/$renterId"
                        } else {
                            "renter_list"
                        }
                ) {

                    // =====================================================
                    // RENTER LIST
                    // =====================================================

                    composable("renter_list") {

                        RenterListScreen(
                            onBackClick = {
                                finish()
                            },
                            onAddRenterClick = {
                                navController.navigate("add_renter")
                            },
                            onRenterClick = { renterId ->

                                navController.navigate(
                                    "renter_details/$renterId"
                                )
                            },
                            onHistoryClick = {

                                navController.navigate(
                                    "renter_history"
                                )
                            }
                        )
                    }

                    // =====================================================
                    // ADD RENTER
                    // =====================================================

                    composable("add_renter") {

                        AddEditRenterScreen(
                            renterId = null,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSaveSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // =====================================================
                    // EDIT RENTER
                    // =====================================================

                    composable(
                        route = "edit_renter/{renterId}",
                        arguments = listOf(
                            navArgument("renterId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val editRenterId =
                            backStackEntry.arguments
                                ?.getString("renterId")
                                ?: return@composable

                        AddEditRenterScreen(
                            renterId = editRenterId,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSaveSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

// =====================================================
// RENTER DETAILS
// =====================================================

                    composable(
                        route = "renter_details/{renterId}",
                        arguments = listOf(
                            navArgument("renterId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val detailsRenterId =
                            backStackEntry.arguments
                                ?.getString("renterId")
                                ?: return@composable

                        RenterDetailsScreen(
                            renterId = detailsRenterId,

                            onBackClick = {
                                navController.popBackStack()
                            },

                            onEditClick = {
                                navController.navigate(
                                    "edit_renter/$detailsRenterId"
                                )
                            },

                            onLedgerClick = {
                                navController.navigate(
                                    "renter_ledger/$detailsRenterId"
                                )
                            }
                        )
                    }

                    // =====================================================
                    // RENTER HISTORY
                    // =====================================================

                    composable("renter_history") {

                        RenterHistoryScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onRenterClick = { historyRenterId ->

                                navController.navigate(
                                    "renter_details/$historyRenterId"
                                )
                            }
                        )
                    }

                    // =====================================================
                    // RENTER LEDGER
                    // =====================================================

                    composable(
                        route = "renter_ledger/{renterId}",
                        arguments = listOf(
                            navArgument("renterId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val ledgerRenterId =
                            backStackEntry.arguments
                                ?.getString("renterId")
                                ?: return@composable

                        RenterLedgerScreen(
                            renterId = ledgerRenterId,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
