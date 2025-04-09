package com.example.products.presentation.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsNavGraphViewModel @Inject constructor() : ViewModel() {

    // track the last 3 product titles that the user viewed in a session
    private val _recentlyViewed = MutableLiveData<List<String>>(emptyList())
    val recentlyViewed: LiveData<List<String>> get() = _recentlyViewed

    init {
        Log.d("ProductsNavGraphViewModel", "NavGraph ViewModel init ...")
    }

    fun addViewedProduct(title: String) {
        val currentList: List<String>? = _recentlyViewed.value
        val current: List<String> = if (currentList != null) {
            currentList
        } else {
            emptyList()
        }

        val updated = (listOf(title) + current).distinct().take(3)
        _recentlyViewed.value = updated

    }
}