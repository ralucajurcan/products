package com.example.products.core.usecase

import com.example.products.core.repository.ProductRepository

class GetAllProducts(private val productRepository: ProductRepository) {

    operator fun invoke() = productRepository.getAllProducts()
}