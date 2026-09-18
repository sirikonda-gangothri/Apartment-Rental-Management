package com.example.apartmentrentalmanagement.screens.rent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class RentManagementActivity : ComponentActivity() {

    private val rentViewModel: RentViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        val renterId =
            intent.getStringExtra("renterId")

        setContent {

            RentNavigation(
                renterId = renterId,
                viewModel = rentViewModel,
                onFinish = {
                    finish()
                }
            )
        }
    }
}


@Composable
private fun RentNavigation(
    renterId: String?,
    viewModel: RentViewModel,
    onFinish: () -> Unit
) {

    val navController =
        rememberNavController()

    val startDestination =
        if (renterId != null) {
            "rent_details/$renterId"
        } else {
            "rent_tracker"
        }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        // =====================================================
        // RENT TRACKER
        // =====================================================

        composable(
            route = "rent_tracker"
        ) {

            RentTrackerScreen(

                viewModel = viewModel,

                onBackClick = {
                    onFinish()
                },

                onRenterClick = { selectedRenterId ->

                    navController.navigate(
                        "rent_details/$selectedRenterId"
                    )
                }
            )
        }


        // =====================================================
        // RENT DETAILS
        // =====================================================

        composable(
            route = "rent_details/{renterId}",
            arguments = listOf(
                navArgument("renterId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->


            val selectedRenterId =
                backStackEntry.arguments
                    ?.getString("renterId")


            if (selectedRenterId == null) {
                return@composable
            }


            RentDetailsScreen(

                renterId = selectedRenterId,

                viewModel = viewModel,

                onBackClick = {

                    navController.popBackStack()
                },

                onAddPaymentClick = {

                    navController.navigate(
                        "rent_payment/$selectedRenterId"
                    )
                },

                onViewLedgerClick = {

                    navController.navigate(
                        "rent_ledger/$selectedRenterId"
                    )
                }
            )
        }


        // =====================================================
        // RECORD PAYMENT
        // =====================================================

        composable(
            route = "rent_payment/{renterId}",
            arguments = listOf(
                navArgument("renterId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->


            val selectedRenterId =
                backStackEntry.arguments
                    ?.getString("renterId")


            if (selectedRenterId == null) {
                return@composable
            }


            RentPaymentScreen(

                renterId = selectedRenterId,

                viewModel = viewModel,

                onBackClick = {

                    navController.popBackStack()
                },

                onPaymentSaved = {

                    /*
                     * Payment was successfully saved.
                     *
                     * Go back to Rent Details.
                     *
                     * The ViewModel listener will update
                     * the payment/status information.
                     */

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // RENT LEDGER
        // =====================================================

        composable(
            route = "rent_ledger/{renterId}",
            arguments = listOf(
                navArgument("renterId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->


            val selectedRenterId =
                backStackEntry.arguments
                    ?.getString("renterId")


            if (selectedRenterId == null) {
                return@composable
            }


            RentLedgerScreen(

                renterId = selectedRenterId,

                viewModel = viewModel,

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }
    }
}

