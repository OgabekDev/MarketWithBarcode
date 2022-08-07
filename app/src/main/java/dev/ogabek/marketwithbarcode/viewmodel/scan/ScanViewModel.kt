package dev.ogabek.marketwithbarcode.viewmodel.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateList
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(private val repository: ScanRepository): ViewModel() {

    private val _isHave = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val isHave = _isHave

    fun isHave() = viewModelScope.launch {
        _isHave.value = UiStateList.LOADING
        try {
            val response = repository.getAllProducts()
            _isHave.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _isHave.value = UiStateList.ERROR(e.message.toString())
        }
    }

    private val _product = MutableStateFlow<UiStateObject<Product>>(UiStateObject.EMPTY)
    val product = _product

    fun getProduct(barCode: Long) = viewModelScope.launch {
        _product.value = UiStateObject.LOADING
        try {
            val response = repository.getProduct(barCode)
            _product.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _product.value = UiStateObject.ERROR(e.message.toString())
        }
    }

}