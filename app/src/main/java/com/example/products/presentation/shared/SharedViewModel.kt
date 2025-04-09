package com.example.products.presentation.shared

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
    Activity View-Model
 */
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _selectedProductId = MutableLiveData<Long>()
    val selectedProductId: LiveData<Long> get() = _selectedProductId

    init {
        Log.d("SharedViewModel", "Activity ViewModel init ...")
    }

    fun selectProduct(productId: Long) {
        _selectedProductId.value = productId
    }
}