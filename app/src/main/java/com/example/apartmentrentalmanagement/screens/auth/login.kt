package com.example.apartmentrentalmanagement.screens.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apartmentrentalmanagement.screens.dashboard.DashboardActivity
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            ApartmentRentalManagementTheme {
                LoginScreen()
            }
        }
    }

    @Composable
    fun LoginScreen() {

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Welcome Back!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login to your Nivasa account",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text("Email")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text("Password")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    // Forgot password will be implemented later
                }
            ) {
                Text("Forgot Password?")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    when {
                        email.isBlank() -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Please enter your email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        password.isBlank() -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Please enter your password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {

                            auth.signInWithEmailAndPassword(
                                email,
                                password
                            ).addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Dashboard navigation will be added next
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            DashboardActivity::class.java
                                        )
                                    )

                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception?.message
                                            ?: "Login failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    startActivity(
                        Intent(this@LoginActivity, SignupActivity::class.java)
                    )
                }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginPreview() {
        ApartmentRentalManagementTheme {
            LoginScreen()
        }
    }

}
