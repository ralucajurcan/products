package com.example.products.presentation.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.common.model.Product
import com.example.products.core.usecase.UseCases
import com.example.products.core.validation.ProductValidator
import com.example.products.framework.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
    Fragment ViewModel
*/
@HiltViewModel // VM that receives dependencies via constructor injection
class ProductViewModel @Inject constructor(
    private val useCases: UseCases,
    private val apiService: ApiService
) : ViewModel() {

    // data holder that the UI can observe and holds a list of products - don't let the UI change the data directly
    private val _products = MutableLiveData<List<Product>>()

    private val _validationError = MutableLiveData<String?>()

    private val _saved = MutableStateFlow(false)

    // getter
    val products: LiveData<List<Product>> get() = _products

    val validationError: LiveData<String?> get() = _validationError

    // flag that tells the UI a product has been successfully saved
    // if true, the Fragment might show a Toast or navigate away
    val saved: StateFlow<Boolean> get() = _saved

    // called auto when the VM is created; loads existing products immediately
    init {
        Log.d("ProductViewModel", "Fragment ViewModel init ...")
        loadProducts()
    }

    // launches a coroutine which is tied to the VM scope (lifecycle)
    // posts the new list of products to the UI
    private fun loadProducts() {
        viewModelScope.launch {
            useCases.getAllProducts().collect { productList ->
                _products.postValue(productList)
            }
        }
    }

    // saves the product and notifies the UI; then refreshes the products list
    fun saveProduct(product: Product) {
        val errorMessage = validateProduct(product)

        if (errorMessage != null) {
            _validationError.value = errorMessage
            return
        }

        viewModelScope.launch {
            useCases.addProduct(product)
            _saved.value = true
            // clear errors
            _validationError.value = null
            loadProducts()
        }
    }

    // retrieves the product and notifies the UI
    fun getProduct(productId: Long): LiveData<Product?> {
        val result = MutableLiveData<Product?>()
        viewModelScope.launch {
            val product = useCases.getProduct(productId)
            result.postValue(product)
        }
        return result
    }

    fun syncProductListFromServer() {
        viewModelScope.launch {
            try {
                // fetch data from fake api
                val response = apiService.fetchFakeProducts()

                // randomly extract 5 products
                val randomProducts = response.products.shuffled().take(5)

                // save them to the db
                randomProducts.forEach { prod ->
                    val product = Product(
                        title = prod.title,
                        description = prod.description,
                        imageUrl = prod.thumbnail,
                        creationTime = System.currentTimeMillis(),
                        updateTime = System.currentTimeMillis()
                    )
                    useCases.addProduct(product)
                }

                // get the data from db
                loadProducts()

                // Avoid calling this directly in VM -> inject the Logger interface (can be mocked in tests)
                Log.d("ProductViewModel", "Get products from API, save them to DB and load them")
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Sync products list from server failed", e)
            }
        }
    }

    private fun validateProduct(product: Product): String? {
        return when {
            !ProductValidator.isValidTitle(product.title) -> "Title cannot be blank"
            !ProductValidator.isValidDescription(product.description) -> "Description must be at least 5 chars long"
            !ProductValidator.isValidImageUrl(product.imageUrl) -> "Image URL must start with http or https"
            else -> null
        }
    }

    // viewModelScope is lifecycle aware - if the VM is destroyed (ex Activity is gone), coroutines
    // are cancelled automatically

    // LiveData - beginner friendly
    // StateFlow - advanced, modern
}