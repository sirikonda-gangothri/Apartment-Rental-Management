package com.example.apartmentrentalmanagement.screens.rent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentDetailsScreen(
    renterId: String,
    viewModel: RentViewModel,
    onBackClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onViewLedgerClick: () -> Unit
) {

    val selectedMonth by
    viewModel.selectedMonth.collectAsState()

    val selectedYear by
    viewModel.selectedYear.collectAsState()


    val renter =
        viewModel.getRenter(renterId)

    val rentStatus =
        viewModel.getRentStatus(renterId)


    // =========================================================
    // RENTER NOT FOUND
    // =========================================================

    if (renter == null || rentStatus == null) {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {
                        Text("Rent Details")

                    },

                    navigationIcon = {

                        IconButton(
                            onClick = onBackClick
                        ) {

                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                Text(
                    text = "Renter information not available"
                )
            }
        }

        return
    }


    val payments =
        viewModel
            .getPaymentsForRenter(renterId)
            .filter { payment ->

                payment.month ==
                        selectedMonth.toString() &&
                        payment.year ==
                        selectedYear
            }


    // =========================================================
    // UI
    // =========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Rent Details")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(
                    text = renter.name
                )

                Text(
                    text =
                        renter.propertyName
                )

                Text(
                    text =
                        "Flat ${renter.flatNumber}"
                )

                Text(
                    text =
                        "${getMonthName(selectedMonth)} $selectedYear"
                )
            }


            // =================================================
            // SUMMARY
            // =================================================

            item {

                RentDetailAmount(
                    title = "Monthly Rent",
                    amount =
                        rentStatus.expectedRent
                )

                RentDetailAmount(
                    title = "Paid",
                    amount =
                        rentStatus.paidAmount
                )

                RentDetailAmount(
                    title = "Pending",
                    amount =
                        rentStatus.pendingAmount
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text("Status")

                    Text(
                        text =
                            rentStatus.paymentStatus
                    )
                }
            }


            // =================================================
            // PAYMENTS
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = "Payments This Month"
                )
            }


            if (payments.isEmpty()) {

                item {

                    Text(
                        text =
                            "No payments recorded for this month."
                    )
                }

            } else {

                items(
                    items = payments,
                    key = { it.id }
                ) { payment ->

                    PaymentRow(
                        payment = payment
                    )
                }
            }


            // =================================================
            // ADD PAYMENT
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Button(

                    onClick =
                        onAddPaymentClick,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        rentStatus.pendingAmount > 0
                ) {

                    Text("Add Payment")
                }
            }


            // =================================================
            // LEDGER
            // =================================================

            item {

                Button(

                    onClick =
                        onViewLedgerClick,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("View Ledger")
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }
        }
    }
}


@Composable
private fun RentDetailAmount(
    title: String,
    amount: Double
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(text = title)

        Text(
            text =
                formatCurrency(amount)
        )
    }
}


@Composable
private fun PaymentRow(
    payment: RentPayment
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text =
                        payment.paymentDate
                )

                Text(
                    text =
                        payment.paymentMethod
                )

                if (payment.notes.isNotBlank()) {

                    Text(
                        text =
                            payment.notes
                    )
                }
            }

            Text(
                text =
                    formatCurrency(
                        payment.amountPaid
                    )
            )
        }

        HorizontalDivider()
    }
}


private fun getMonthName(
    month: Int
): String {

    return when (month) {

        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"

        else -> ""
    }
}


private fun formatCurrency(
    amount: Double
): String {

    return NumberFormat
        .getCurrencyInstance(
            Locale("en", "IN")
        )
        .format(amount)
}