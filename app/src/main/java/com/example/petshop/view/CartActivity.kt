package com.example.petshop.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.petshop.R
import com.example.petshop.model.CartItemModel
import com.example.petshop.model.OrderModel
import com.example.petshop.repository.CartRepositoryImpl
import com.example.petshop.repository.OrderRepositoryImpl
import com.example.petshop.viewmodel.CartViewModel
import com.example.petshop.viewmodel.CartViewModelFactory
import com.example.petshop.viewmodel.OrderViewModelFactory
import com.example.petshop.ui.theme.PetshopTheme
import com.example.petshop.viewmodel.OrderViewModel


class CartActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val orderRepo = OrderRepositoryImpl()

        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        cartViewModel.loadCartItems()

        setContent {
            PetshopTheme {  // Changed theme name
                CartScreen(cartViewModel = cartViewModel, orderViewModel = orderViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel, orderViewModel: OrderViewModel) {
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val errorMessage by cartViewModel.error.observeAsState()
    val orderError by orderViewModel.error.observeAsState()
    val context = LocalContext.current

    val totalPrice = cartItems.sumOf { it.productPrice * it.quantity }

    // Show Toast for order errors or success
    LaunchedEffect(orderError) {
        orderError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            orderViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Pet Cart", fontSize = 20.sp) },  // Updated title
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5D4037)  // Brown color for pet theme
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = com.example.petshop.R.drawable.petpaw), // Changed from 'logo' to standard pet icon
                        contentDescription = "Pet Store",
                        modifier = Modifier.padding(start = 12.dp)
                    )

                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .background(Color(0xFFEFEBE9))  // Light brown background
            ) {
                if (!errorMessage.isNullOrEmpty()) {
                    Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }

                if (cartItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.petcart), // Add pet-themed empty cart icon
                            contentDescription = "Empty Cart",
                            tint = Color(0xFF8D6E63),
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "Your pet cart is empty!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF5D4037),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Add some treats or toys for your furry friend!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5D4037))

                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems) { item ->
                            PetCartItemCard(  // Renamed to PetCartItemCard
                                item = item,
                                onIncrease = {
                                    cartViewModel.updateQuantity(item.id, item.quantity + 1)
                                },
                                onDecrease = {
                                    if (item.quantity > 1) {
                                        cartViewModel.updateQuantity(item.id, item.quantity - 1)
                                    }
                                },
                                onRemove = {
                                    cartViewModel.removeCartItem(item.id)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium, color = Color(0xFF5D4037))
                        Text("Rs. ${"%.2f".format(totalPrice)}", fontSize = 20.sp, style = MaterialTheme.typography.titleLarge, color = Color(0xFF5D4037))
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        onClick = {
                            if (cartItems.isNotEmpty()) {
                                val userId = "USR001"  // Replace with actual user ID from auth
                                val order = OrderModel(
                                    orderId = "",  // will be set by Firebase on backend
                                    userId = userId,
                                    items = cartItems,
                                    totalAmount = totalPrice,
                                    orderStatus = "Pending"
                                )
                                orderViewModel.placeOrder(order)
                                Toast.makeText(context, "Order placed! Your pet will be happy!", Toast.LENGTH_SHORT).show()  // Updated message
                                // Optionally clear cart after order placed
                                // cartViewModel.clearCart()
                            } else {
                                Toast.makeText(context, "Your pet cart is empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D4037),  // Brown color
                            contentColor = Color.White
                        )
                    ) {
                        Text("Checkout for Pets")
                    }
                }
            }
        }
    )
}

@Composable
fun PetCartItemCard(  // Renamed and modified for pet theme
    item: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD7CCC8)  // Light brown card background
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = "Rs. ${item.productPrice}",
                    fontSize = 14.sp,
                    color = Color(0xFF8D6E63)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF8D6E63),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Decrease")
                    }
                    Text(
                        text = "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 16.sp,
                        color = Color(0xFF5D4037)
                    )
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF8D6E63),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF5D4037),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}