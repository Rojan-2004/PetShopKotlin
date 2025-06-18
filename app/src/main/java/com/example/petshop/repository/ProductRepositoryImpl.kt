package com.example.petshop.repository



import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.example.petshop.model.ProductModule
import com.example.petshop.repository.ProductRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductRepositoryImpl : ProductRepository {
    val database = FirebaseDatabase.getInstance()
    val ref = database.reference.child("products")
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dahidci6o",
            "api_key" to "158758959194845",
            "api_secret" to "Oo50NMS-vrURt3gETED4ibe21uo"
        )
    )

    override fun addProduct(
        model: ProductModule,
        callback: (Boolean, String) -> Unit
    ) {
        val id = ref.push().key.toString()
        model.productId = id
        ref.child(model.productId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product added successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    //Create - setValue()
    //Update - updateChildren()
    //Delete - removeValue()
    override fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product updated successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun getProductById(
        productId: String,
        callback: (Boolean, String, ProductModule?) -> Unit
    ) {
        ref.child(productId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductModule::class.java)
                    if (product != null) {
                        callback(true, "product fetched", product)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }

        })
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModule?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allProducts = mutableListOf<ProductModule>()
                    for (eachProduct in snapshot.children) {
                        var products = eachProduct.getValue(ProductModule::class.java)
                        if (products != null) {
                            allProducts.add(products)
                        }
                    }
                    callback(true, "product fetched", allProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }

        })
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        TODO("Not yet implemented")
    }
}