package com.example.products.presentation.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.common.model.Product
import com.example.products.core.usecase.UseCases
import com.example.products.core.validation.ProductValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

/*
    Fragment ViewModel
*/
@HiltViewModel // VM that receives dependencies via constructor injection
class ProductViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private val logger = LoggerFactory.getLogger("ProductViewModel")

    // data holder that the UI can observe and holds a list of products - don't let the UI change the data directly
    private val _products = MutableLiveData<List<Product>>()

    private val _validationError = MutableLiveData<String?>()

    private val _saved = MutableStateFlow(false)

    private val _isLoading = MutableLiveData<Boolean>()

    // getter
    val products: LiveData<List<Product>> get() = _products

    val validationError: LiveData<String?> get() = _validationError

    // flag that tells the UI a product has been successfully saved
    // if true, the Fragment might show a Toast or navigate away
    val saved: StateFlow<Boolean> get() = _saved

    val isLoading: LiveData<Boolean> get() = _isLoading

    // called auto when the VM is created; loads existing products immediately
    init {
        Log.d("ProductViewModel", "Fragment ViewModel init ...")
        loadProducts()
    }

    // launches a coroutine which is tied to the VM scope (lifecycle)
    // posts the new list of products to the UI
    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true

            useCases.getAllProducts().collect { productList ->
                _products.postValue(productList)
                _isLoading.postValue(false)
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

    private fun validateProduct(product: Product): String? {
        return when {
            !ProductValidator.isValidTitle(product.title) -> "Title cannot be blank"
            !ProductValidator.isValidDescription(product.description) -> "Description must be at least 5 chars long"
            !ProductValidator.isValidImageUrl(product.imageUrl) -> "Image URL must start with http or https"
            else -> null
        }
    }

    fun syncProductsFromNetwork() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                useCases.syncProducts()
            } catch (e: Exception) {
                logger.error("[ProductViewModel] error: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // viewModelScope is lifecycle aware - if the VM is destroyed (ex Activity is gone), coroutines
    // are cancelled automatically

    // LiveData - beginner friendly
    // StateFlow - advanced, modern
}