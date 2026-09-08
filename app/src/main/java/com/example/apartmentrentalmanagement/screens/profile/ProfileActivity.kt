package com.example.apartmentrentalmanagement.screens.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apartmentrentalmanagement.screens.auth.LoginActivity
import com.example.apartmentrentalmanagement.ui.theme.ApartmentRentalManagementTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : ComponentActivity() {

    private val TAG = "ProfileActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            ApartmentRentalManagementTheme {
                ProfileScreen()
            }
        }
    }

    @Composable
    fun ProfileScreen() {

        var showLogoutDialog by remember {
            mutableStateOf(false)
        }

        var fullName by remember {
            mutableStateOf("")
        }

        var phoneNumber by remember {
            mutableStateOf("")
        }

        var isLoading by remember {
            mutableStateOf(true)
        }

        var isProfileMissing by remember {
            mutableStateOf(false)
        }

        val currentUser = auth.currentUser

        LaunchedEffect(currentUser) {
            if (currentUser != null) {
                val uid = currentUser.uid
                Log.d(TAG, "Fetching data for UID: $uid")
                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Log.d(TAG, "Document found for UID: $uid")
                            fullName = document.getString("fullName") ?: ""
                            phoneNumber = document.getString("phoneNumber") ?: ""
                            isProfileMissing = false
                        } else {
                            Log.e(TAG, "No document found in 'users' collection for UID: $uid")
                            isProfileMissing = true
                        }
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Firestore fetch failed for UID: $uid", exception)
                        isLoading = false
                        Toast.makeText(
                            this@ProfileActivity,
                            exception.message ?: "Failed to load profile",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Log.w(TAG, "No user logged in")
                isLoading = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Profile",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isProfileMissing) {
                        Text(
                            text = "Complete Your Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                if (fullName.isNotBlank() && phoneNumber.isNotBlank()) {
                                    isLoading = true
                                    val uid = currentUser?.uid
                                    if (uid != null) {
                                        val userData = hashMapOf(
                                            "fullName" to fullName,
                                            "email" to (currentUser.email ?: ""),
                                            "phoneNumber" to phoneNumber
                                        )
                                        db.collection("users").document(uid).set(userData)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                isProfileMissing = false
                                                Toast.makeText(this@ProfileActivity, "Profile saved!", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                isLoading = false
                                                Toast.makeText(this@ProfileActivity, "Failed to save: ${it.message}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(this@ProfileActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Profile")
                        }
                    } else {
                        Text(
                            text = fullName.ifBlank { "No name available" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentUser?.email ?: "No email available",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = phoneNumber.ifBlank { "No phone number available" },
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            showLogoutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {

            AlertDialog(
                onDismissRequest = {
                    showLogoutDialog = false
                },
                title = {
                    Text("Logout")
                },
                text = {
                    Text("Are you sure you want to logout?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {

                            auth.signOut()

                            startActivity(
                                Intent(
                                    this@ProfileActivity,
                                    LoginActivity::class.java
                                )
                            )

                            finish()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
