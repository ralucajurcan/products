package com.example.products.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.common.model.Product
import com.example.products.core.usecase.UseCases
import com.example.products.core.validation.ProductValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    val products = useCases.getAllProducts()

    private val _uiState = MutableStateFlow(
        ProductUiState(isLoading = false, isSaved = false, validationError = null)
    )
    val uiState: StateFlow<ProductUiState> = _uiState

    // saves the product and notifies the UI; then refreshes the products list
    fun saveProduct(product: Product) {
        val errorMessage = validateProduct(product)

        if (errorMessage != null) {
            _uiState.update { it.copy(validationError = errorMessage) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            useCases.addProduct(product)
            _uiState.update { it.copy(
                isSaved = true,
                validationError = null)
            }
        }
    }

    fun getProduct(productId: Long) = useCases.getProductAsFlow(productId)

    private fun validateProduct(product: Product): String? {
        return when {
            !ProductValidator.isValidTitle(product.title) -> "Title cannot be blank"
            !ProductValidator.isValidDescription(product.description) -> "Description must be at least 5 chars long"
            !ProductValidator.isValidImageUrl(product.imageUrl) -> "Image URL must start with http or https"
            else -> null
        }
    }

    fun syncProductsFromNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            try {
                useCases.syncProducts()
            } catch (e: Exception) {
                logger.error("[ProductViewModel] error: ", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // viewModelScope is lifecycle aware - if the VM is destroyed (ex Activity is gone), coroutines
    // are cancelled automatically

    // LiveData - beginner friendly
    // StateFlow - advanced, modern
}