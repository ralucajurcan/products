package com.example.products.core.validation

object ProductValidator {

    fun isValidTitle(title: String): Boolean = title.isNotBlank()

    fun isValidDescription(description: String): Boolean = description.length >= 5

    fun isValidImageUrl(url: String): Boolean = url.startsWith("http://") || url.startsWith("https://")

}