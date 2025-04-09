package com.example.products.core.usecase

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductRepository

class AddProduct(private val productRepository: ProductRepository) {

    suspend operator fun invoke(product: Product) = productRepository.addProduct(product)
}