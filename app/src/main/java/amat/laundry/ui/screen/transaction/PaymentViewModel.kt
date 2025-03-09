package amat.laundry.ui.screen.transaction

import amat.laundry.data.ProductCart
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CartRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _stateProduct: MutableStateFlow<UiState<List<ProductCart>>> =
        MutableStateFlow(UiState.Loading)
    val stateProduct: StateFlow<UiState<List<ProductCart>>>
        get() = _stateProduct

    private val _stateUi: MutableStateFlow<PaymentUi> =
        MutableStateFlow(PaymentUi())
    val stateUi: StateFlow<PaymentUi>
        get() = _stateUi

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed


    fun getProduct() {
        viewModelScope.launch {
            _stateProduct.value = UiState.Loading
            try {
                val cart = cartRepository.getCartList()
                _stateProduct.value = UiState.Success(cart)
            } catch (e: Exception) {
                Log.e("payment", e.message.toString())
                _stateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun checkPayment() {
        TODO("Not yet implemented")
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}