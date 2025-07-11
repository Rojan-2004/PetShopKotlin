package com.example.petshop.view


import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.petshop.model.UserModule
import com.example.petshop.repository.UserRepositoryImpl
import com.example.petshop.viewmodel.UserViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                RegBody(innerPadding)
            }
        }
    }
}

@Composable
fun RegBody(innerPaddingValues: PaddingValues) {
    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    var firstName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Country") }

    val options = listOf("Nepal", "India", "China")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Light background
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Create an Account",
            color = Color(0xFF2E7D32),
        )

        Spacer(modifier = Modifier.height(30.dp))

        // First + Last Name Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("First Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                placeholder = { Text("Last Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email (e.g. abc@gmail.com)") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedOptionText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
                    .clickable { expanded = true },
                placeholder = { Text("Select Country") },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledIndicatorColor = Color.Gray
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                userViewModel.register(email, password) { success, message, userId ->
                    if (success) {
                        val userModel = UserModule(
                            userId, email, firstName, lastname,
                            "Male", "9841994110", selectedOptionText
                        )
                        userViewModel.addUserToDatabase(userId, userModel) { success2, msg2 ->
                            Toast.makeText(context, msg2, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
        ) {
            Text(text = "Register", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable {
                activity?.finish() // You can also navigate to login
            },
            color = Color(0xFF2E7D32)
        )
    }
}
