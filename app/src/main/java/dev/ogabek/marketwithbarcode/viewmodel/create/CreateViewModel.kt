package dev.ogabek.marketwithbarcode.viewmodel.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(private val repository: CreateRepository) : ViewModel() {

    private val _create = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val create = _create

    fun create(product: Product) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val create = repository.createProduct(product)
            _create.value = UiStateObject.SUCCESS(create)
        } catch (e: Exception) {
            e.printStackTrace()
            _create.value = UiStateObject.ERROR(e.message.toString())
        }
    }

}