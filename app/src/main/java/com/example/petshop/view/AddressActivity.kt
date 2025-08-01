package com.example.petshop.view

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.petshop.model.AddressModel
import com.example.petshop.repository.AddressRepositoryImpl
import com.example.petshop.ui.theme.PetshopTheme
import com.example.petshop.viewmodel.AddressViewModel
import java.util.*

class AddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetshopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AddressScreen(userId = "demo_user_id") // Replace with real userId
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(userId: String) {
    val viewModel = remember { AddressViewModel(AddressRepositoryImpl()) }
    val context = LocalContext.current

    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var addresses by remember { mutableStateOf(emptyList<AddressModel>()) }

    fun loadAddresses() {
        viewModel.getAddresses(userId) {
            addresses = it
        }
    }

    LaunchedEffect(Unit) {
        loadAddresses()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Address Book") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = if (selectedId == null) "Add New Address" else "Update Address",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = addressLine,
                        onValueChange = { addressLine = it },
                        label = { Text("Address Line") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("District") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Postal Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val id = selectedId ?: UUID.randomUUID().toString()
                            val address = AddressModel(
                                id = id,
                                userId = userId,
                                street = addressLine,
                                city = city,
                                district = state,
                                postalCode = postalCode
                            )

                            val action = if (selectedId == null) viewModel::addAddress else viewModel::updateAddress

                            action(address) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    addressLine = ""
                                    city = ""
                                    state = ""
                                    postalCode = ""
                                    selectedId = null
                                    loadAddresses()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedId == null) "Save Address" else "Update Address")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Your Saved Addresses",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "No addresses",
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "No saved addresses yet",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(addresses) { address ->
                        AddressCard(
                            address = address,
                            onEdit = {
                                selectedId = address.id
                                addressLine = address.street
                                city = address.city
                                state = address.district
                                postalCode = address.postalCode
                            },
                            onDelete = {
                                viewModel.deleteAddress(address.id) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) loadAddresses()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddressCard(
    address: AddressModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Address: ${address.street}")
            Text("City: ${address.city}")
            Text("District: ${address.district}")
            Text("Postal Code: ${address.postalCode}")
        }
    }
}