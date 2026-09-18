package com.example.apartmentrentalmanagement.screens.rent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentPaymentScreen(
    renterId: String,
    viewModel: RentViewModel,
    onBackClick: () -> Unit,
    onPaymentSaved: () -> Unit
) {

    val selectedMonth by
    viewModel.selectedMonth.collectAsState()

    val selectedYear by
    viewModel.selectedYear.collectAsState()

    val errorMessage by
    viewModel.errorMessage.collectAsState()


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
                        Text(text="Record Payment",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
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
                    text = "Renter information not found"
                )
            }
        }

        return
    }


    // =========================================================
    // FORM STATE
    // =========================================================

    var amountText by remember {
        mutableStateOf("")
    }

    var paymentDate by remember {

        mutableStateOf(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var selectedPaymentMethod by remember {
        mutableStateOf("Cash")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var paymentMethodExpanded by remember {
        mutableStateOf(false)
    }

    var validationError by remember {
        mutableStateOf<String?>(null)
    }


    val paymentMethods =
        listOf(
            "Cash",
            "UPI",
            "Bank Transfer",
            "Cheque"
        )


    // =========================================================
    // UI
    // =========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(text="Record Payment",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
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
                .verticalScroll(
                    rememberScrollState()
                ),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // =================================================
            // RENTER INFORMATION
            // =================================================

            Text(
                text = renter.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "${renter.propertyName} • Flat ${renter.flatNumber}"
            )

            Text(
                text =
                    "${getMonthName(selectedMonth)} $selectedYear"
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // PAYMENT SUMMARY
            // =================================================

            PaymentSummaryRow(
                title = "Expected Rent",
                amount = rentStatus.expectedRent
            )

            PaymentSummaryRow(
                title = "Already Paid",
                amount = rentStatus.paidAmount
            )

            PaymentSummaryRow(
                title = "Pending",
                amount = rentStatus.pendingAmount
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // AMOUNT
            // =================================================

            OutlinedTextField(

                value = amountText,

                onValueChange = {
                    amountText = it
                    validationError = null
                    viewModel.clearError()
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Amount Received")
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    )
            )


            // =================================================
            // PAYMENT DATE
            // =================================================

            OutlinedTextField(

                value = paymentDate,

                onValueChange = {
                    paymentDate = it
                    validationError = null
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Payment Date")
                },

                singleLine = true
            )


            // =================================================
            // PAYMENT METHOD
            // =================================================

            ExposedDropdownMenuBox(

                expanded = paymentMethodExpanded,

                onExpandedChange = {
                    paymentMethodExpanded =
                        !paymentMethodExpanded
                }
            ) {

                OutlinedTextField(

                    value = selectedPaymentMethod,

                    onValueChange = {},

                    readOnly = true,

                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),

                    label = {
                        Text("Payment Method")
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    paymentMethodExpanded
                            )
                    }
                )


                ExposedDropdownMenu(

                    expanded = paymentMethodExpanded,

                    onDismissRequest = {
                        paymentMethodExpanded = false
                    }
                ) {

                    paymentMethods.forEach { method ->

                        DropdownMenuItem(

                            text = {
                                Text(method)
                            },

                            onClick = {

                                selectedPaymentMethod =
                                    method

                                paymentMethodExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            // =================================================
            // NOTES
            // =================================================

            OutlinedTextField(

                value = notes,

                onValueChange = {
                    notes = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Notes")
                },

                minLines = 3
            )


            // =================================================
            // ERROR
            // =================================================

            val displayedError =
                validationError ?: errorMessage

            if (!displayedError.isNullOrBlank()) {

                Text(
                    text = displayedError
                )
            }


            // =================================================
            // RECORD BUTTON
            // =================================================

            Button(

                onClick = {

                    val amount =
                        amountText.toDoubleOrNull()


                    when {

                        amount == null -> {

                            validationError =
                                "Enter a valid payment amount"
                        }


                        amount <= 0.0 -> {

                            validationError =
                                "Payment amount must be greater than 0"
                        }


                        amount > rentStatus.pendingAmount -> {

                            validationError =
                                "Payment cannot be greater than the pending amount"
                        }


                        paymentDate.isBlank() -> {

                            validationError =
                                "Enter the payment date"
                        }


                        else -> {

                            validationError = null

                            viewModel.recordPayment(

                                renter = renter,

                                amountPaid = amount,

                                paymentDate = paymentDate,

                                paymentMethod =
                                    selectedPaymentMethod,

                                notes = notes,

                                onSuccess = {
                                    onPaymentSaved()
                                }
                            )
                        }
                    }
                },

                modifier = Modifier.fillMaxWidth(),

                enabled =
                    rentStatus.pendingAmount > 0
            ) {

                Text("Record Payment")
            }
        }
    }
}


@Composable
private fun PaymentSummaryRow(
    title: String,
    amount: Double
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(text = title)

        Text(
            text = formatCurrency(amount)
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