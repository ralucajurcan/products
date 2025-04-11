package com.example.products.core.validation

import org.junit.Assert.assertTrue
import org.junit.Test

class ProductValidatorTest {

    @Test
    fun titleIsValidTest() {
        assertTrue(ProductValidator.isValidTitle("not blank title"))
    }

    @Test
    fun titleIsInvalidTest() {
        assertTrue(ProductValidator.isValidTitle(""))
    }

}