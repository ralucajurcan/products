package com.example.products.core.usecase

import com.example.products.common.model.Product

// similar to Java records - data classes can be mutable, immutable, inherit from other classes
// has a .copy() method for object creation
data class UseCases(
    val addProduct: AddProduct,
    val getAllProducts: GetAllProducts,
    val getProduct: suspend(Long) -> Product?,
    val syncProducts: suspend () -> Unit
)