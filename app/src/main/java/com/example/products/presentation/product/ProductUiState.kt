package com.example.products.presentation.product

import com.example.products.common.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val validationError: String? = null,
    val isSaved: Boolean = false
)