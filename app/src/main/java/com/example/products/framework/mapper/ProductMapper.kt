package com.example.products.framework.mapper

import com.example.products.common.model.Product
import com.example.products.framework.db.ProductEntity

fun ProductEntity.toProduct(): Product = Product(
    id = this.id,
    title = this.title,
    description = this.description,
    creationTime = this.creationTime,
    updateTime = this.updateTime,
    imageUrl = this.imageUrl
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    creationTime = this.creationTime,
    updateTime = this.updateTime,
    imageUrl = this.imageUrl
)