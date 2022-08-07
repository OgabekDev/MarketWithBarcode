package dev.ogabek.marketwithbarcode.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _products = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val products = _products

    fun getAppProducts() = viewModelScope.launch {
        _products.value = UiStateList.LOADING
        try {
            val products = repository.getAllList()
            _products.value = UiStateList.SUCCESS(products)
        } catch (e: Exception) {
            e.printStackTrace()
            _products.value = UiStateList.ERROR(e.message.toString())
        }
    }

}