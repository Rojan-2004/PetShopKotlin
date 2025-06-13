package com.example.petshop.repository

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
}