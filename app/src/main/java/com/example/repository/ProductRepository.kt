package com.example.petshop.repository

import android.content.Context
import android.net.Uri
import com.example.petshop.model.ProductModule



interface ProductRepository {
    fun addProduct(
        model: ProductModule,
        callback: (Boolean, String) -> Unit
    )

    fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    )

    fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    )

    /*
  success : true,
  message : "product fetched succesfully"
   */
    fun getProductById(
        productId: String,
        callback: (Boolean, String, ProductModule?) -> Unit
    )

    fun getAllProduct(callback: (Boolean, String,
                                 List<ProductModule?>) -> Unit)
    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context,uri: Uri): String?
}
