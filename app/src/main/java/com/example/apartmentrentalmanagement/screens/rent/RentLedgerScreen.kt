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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentLedgerScreen(
    renterId: String,
    viewModel: RentViewModel,
    onBackClick: () -> Unit
) {

    val renter =
        viewModel.getRenter(renterId)

    val payments =
        viewModel.getPaymentsForRenter(renterId)


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(text="Rent Ledger",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
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

        if (renter == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                Text(
                    text =
                        "Renter information not found"
                )
            }

            return@Scaffold
        }


        /*
         * Group all payments by month and year.
         *
         * Example:
         *
         * 9-2026
         * 8-2026
         * 7-2026
         */
        val groupedPayments =
            payments
                .groupBy {
                    "${it.month}-${it.year}"
                }
                .toList()
                .sortedWith(
                    compareByDescending<
                            Pair<String, List<RentPayment>>
                            > {

                        it.second.firstOrNull()?.year
                            ?: 0
                    }.thenByDescending {

                        it.second.firstOrNull()
                            ?.month
                            ?.toIntOrNull()
                            ?: 0
                    }
                )


        LazyColumn(

            modifier =
                Modifier
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
                    text =
                        renter.name
                )

                Text(
                    text =
                        renter.propertyName
                )

                Text(
                    text =
                        "Flat ${renter.flatNumber}"
                )
            }


            if (groupedPayments.isEmpty()) {

                item {

                    Text(
                        text =
                            "No payment history available"
                    )
                }

            } else {

                groupedPayments.forEach { (_, monthPayments) ->

                    val firstPayment =
                        monthPayments.first()

                    val month =
                        firstPayment.month.toIntOrNull()
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
                            expectedRent - paidAmount,
                            0.0
                        )


                    item {

                        LedgerMonthSection(

                            month = month,

                            year = year,

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


            item {

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }
        }
    }
}


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

        Text(
            text =
                "${getMonthName(month)} $year"
        )


        LedgerAmountRow(
            title = "Expected",
            amount = expectedRent
        )

        LedgerAmountRow(
            title = "Paid",
            amount = paidAmount
        )

        LedgerAmountRow(
            title = "Pending",
            amount = pendingAmount
        )


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        Text(
            text = "Payments"
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
                formatCurrency(amount)
        )
    }
}


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

    return "₹${"%,.0f".format(amount)}"
}