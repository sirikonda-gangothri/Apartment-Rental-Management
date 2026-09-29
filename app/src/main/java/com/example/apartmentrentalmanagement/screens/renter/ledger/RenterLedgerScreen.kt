package com.example.apartmentrentalmanagement.screens.renter.ledger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apartmentrentalmanagement.screens.rent.RentPayment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterLedgerScreen(
    renterId: String,
    onBackClick: () -> Unit
) {

    // =========================================================
    // VIEWMODEL
    // =========================================================

    val viewModel: RenterLedgerViewModel =
        viewModel()


    // =========================================================
    // STATE
    // =========================================================

    val isLoading by
    viewModel.isLoading.collectAsState()

    val renter by
    viewModel.renter.collectAsState()

    val payments by
    viewModel.payments.collectAsState()


    // =========================================================
    // LOAD LEDGER
    // =========================================================

    LaunchedEffect(renterId) {

        viewModel.loadLedger(
            renterId
        )
    }


    // =========================================================
    // SCREEN
    // =========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Rent Ledger",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->


        // =====================================================
        // LOADING
        // =====================================================

        if (
            isLoading &&
            renter == null
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        )
                        .padding(16.dp)
            ) {

                Text(
                    text =
                        "Loading renter information..."
                )
            }

            return@Scaffold
        }


        // =====================================================
        // RENTER NOT FOUND
        // =====================================================

        val currentRenter = renter

        if (currentRenter == null) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        )
                        .padding(16.dp)
            ) {

                Text(
                    text =
                        "Renter information not found"
                )
            }

            return@Scaffold
        }


        // =====================================================
        // LEDGER
        // =====================================================

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
                    .padding(
                        horizontal = 16.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // =================================================
            // RENTER INFORMATION
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        currentRenter.name,

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        currentRenter.propertyName
                )

                Text(
                    text =
                        "Flat ${currentRenter.flatNumber}"
                )

                Text(
                    text =
                        "Monthly Rent: ${
                            formatCurrency(
                                currentRenter.monthlyRent
                            )
                        }"
                )
            }


            // =================================================
            // PAYMENT HISTORY
            // =================================================

            if (payments.isEmpty()) {

                item {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "No payment history available"
                    )
                }

            } else {

                // -------------------------------------------------
                // GROUP PAYMENTS BY MONTH AND YEAR
                // -------------------------------------------------

                val groupedPayments =
                    payments
                        .groupBy {

                            "${it.month}-${it.year}"
                        }
                        .toList()
                        .sortedWith(

                            compareByDescending<
                                    Pair<
                                            String,
                                            List<RentPayment>
                                            >
                                    > {

                                it.second
                                    .firstOrNull()
                                    ?.year
                                    ?: 0
                            }

                                .thenByDescending {

                                    it.second
                                        .firstOrNull()
                                        ?.month
                                        ?.toIntOrNull()
                                        ?: 0
                                }
                        )


                // -------------------------------------------------
                // DISPLAY EACH MONTH
                // -------------------------------------------------

                groupedPayments.forEach {

                        (_, monthPayments) ->

                    val firstPayment =
                        monthPayments.first()


                    val month =
                        firstPayment
                            .month
                            .toIntOrNull()
                            ?: 0


                    val year =
                        firstPayment.year


                    val expectedRent =
                        firstPayment.expectedRent


                    val paidAmount =
                        monthPayments.sumOf {

                            it.amountPaid
                        }


                    val pendingAmount =
                        maxOf(

                            expectedRent -
                                    paidAmount,

                            0.0
                        )


                    item {

                        LedgerMonthSection(

                            month =
                                month,

                            year =
                                year,

                            expectedRent =
                                expectedRent,

                            paidAmount =
                                paidAmount,

                            pendingAmount =
                                pendingAmount,

                            payments =
                                monthPayments
                        )
                    }
                }
            }


            // =================================================
            // BOTTOM SPACE
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }
        }
    }
}


// =============================================================
// MONTH SECTION
// =============================================================

@Composable
private fun LedgerMonthSection(
    month: Int,
    year: Int,
    expectedRent: Double,
    paidAmount: Double,
    pendingAmount: Double,
    payments: List<RentPayment>
) {

    Column(

        modifier =
            Modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        // -----------------------------------------------------
        // MONTH
        // -----------------------------------------------------

        Text(
            text =
                "${getMonthName(month)} $year",

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )


        // -----------------------------------------------------
        // EXPECTED
        // -----------------------------------------------------

        LedgerAmountRow(
            title = "Expected",
            amount = expectedRent
        )


        // -----------------------------------------------------
        // PAID
        // -----------------------------------------------------

        LedgerAmountRow(
            title = "Paid",
            amount = paidAmount
        )


        // -----------------------------------------------------
        // PENDING
        // -----------------------------------------------------

        LedgerAmountRow(
            title = "Pending",
            amount = pendingAmount
        )


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        // -----------------------------------------------------
        // PAYMENTS
        // -----------------------------------------------------

        Text(
            text = "Payments",
            fontWeight =
                FontWeight.Bold
        )


        payments
            .sortedByDescending {

                it.paymentDate
            }
            .forEach { payment ->

                PaymentHistoryRow(
                    payment = payment
                )
            }


        HorizontalDivider()
    }
}


// =============================================================
// AMOUNT ROW
// =============================================================

@Composable
private fun LedgerAmountRow(
    title: String,
    amount: Double
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = title
        )

        Text(
            text =
                formatCurrency(
                    amount
                )
        )
    }
}


// =============================================================
// PAYMENT HISTORY ROW
// =============================================================

@Composable
private fun PaymentHistoryRow(
    payment: RentPayment
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

            if (
                payment.notes.isNotBlank()
            ) {

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
}


// =============================================================
// MONTH NAME
// =============================================================

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


// =============================================================
// CURRENCY
// =============================================================

private fun formatCurrency(
    amount: Double
): String {

    return "₹${"%,.0f".format(amount)}"
}