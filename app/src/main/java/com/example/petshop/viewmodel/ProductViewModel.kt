package com.example.petshop.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petshop.model.ProductModule
import com.example.petshop.repository.ProductRepository

class ProductViewModel(val repo: ProductRepository) : ViewModel() {

    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }

    fun addProduct(
        model: ProductModule,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addProduct(model, callback)
    }

    fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProduct(productId, data, callback)
    }

    fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteProduct(productId, callback)
    }

    private val _products = MutableLiveData<ProductModule?>()
    val products: LiveData<ProductModule?> get() = _products


    private var _loading = MutableLiveData<Boolean>()
     var loading = MutableLiveData<Boolean>()
         get() = _loading

    private val _allProducts = MutableLiveData<List<ProductModule?>>()
    val allProducts: LiveData<List<ProductModule?>> get() = _allProducts

    fun getProductById(
        productId: String,
    ) {
        repo.getProductById(productId) { success, msg, data ->
            if (success) {
                _products.postValue(data)
            } else {
                _products.postValue(null)

            }
        }
    }


    fun getAllProduct() {
        _loading.postValue(true)
        repo.getAllProduct()  { success, msg, data ->
            if (success) {
                _loading.postValue(false)
                _allProducts.postValue(data)
            } else {
                _loading.postValue(false)
                _allProducts.postValue(emptyList())

            }
        }
    }


}