package dev.ogabek.marketwithbarcode.viewmodel.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: DetailsRepository): ViewModel() {

    private val _change = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val change = _change

    fun change(barcode: Long, quantity: Int) = viewModelScope.launch {
        _change.value = UiStateObject.LOADING

        try {
            val response = repository.change(barcode, quantity)
            _change.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _change.value = UiStateObject.ERROR(e.message.toString())
        }

    }

}