package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cart
import amat.laundry.data.ProductCart
import amat.laundry.data.User
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CartRepository
import amat.laundry.data.repository.ProductRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddCartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _stateInitProduct: MutableStateFlow<UiState<ProductCart>> =
        MutableStateFlow(UiState.Loading)
    val stateInitProduct: StateFlow<UiState<ProductCart>>
        get() = _stateInitProduct

    private val _stateUi: MutableStateFlow<AddCartUi> =
        MutableStateFlow(AddCartUi())
    val stateUi: StateFlow<AddCartUi>
        get() = _stateUi

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun getDetailProduct(productId: String) {
        viewModelScope.launch {
            _stateInitProduct.value = UiState.Loading
            try {
                val product = productRepository.getProductCategoryDetail(productId)

                var productTemp = ProductCart(
                    productId = product.productId,
                    productName = product.productName,
                    productPrice = product.productPrice,
                    categoryName = product.categoryName,
                    unit = product.unit,
                    qty = 0F,
                    note = ""
                )

                val cart = cartRepository.getCartDetail(productId)

                if (cart != null) {
                    productTemp = productTemp.copy(qty = cart.qty, note = cart.note)
                }

                val totalPrice = productTemp.qty * productTemp.productPrice

                _stateUi.value =
                    stateUi.value.copy(
                        productId = productTemp.productId,
                        qty = if (productTemp.qty == 0F) "" else productTemp.qty.toString(),
                        note = productTemp.note,
                        price = productTemp.productPrice.toString(),
                        totalPrice = totalPrice.toInt().toString()
                    )

                _stateInitProduct.value = UiState.Success(productTemp)

            } catch (e: Exception) {
                Log.e("add cart", e.message.toString())
                _stateInitProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setQty(value: String) {
        clearError()
        var qty = 0F
        val cleanValue = value.replace(",", ".").replace(" ", "")
        //check qty is float or not
        if (cleanValue.toFloatOrNull() != null) {
            qty = cleanValue.toFloat()
            val totalPrice = stateUi.value.price.toFloat() * qty.toFloat()
            _stateUi.value =
                stateUi.value.copy(qty = value, totalPrice = totalPrice.toInt().toString())
        } else {
            _stateUi.value =
                stateUi.value.copy(qty = "", totalPrice = "0")
        }

    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

    fun insertCart() {
        //todo check qty value

        viewModelScope.launch {
            try {
                cartRepository.insert(
                    Cart(
                        productId = stateUi.value.productId,
                        qty = stateUi.value.qty.toFloat(),
                        note = stateUi.value.note
                    )
                )
                _isProsesSuccess.value = ValidationResult(false)
            } catch (e: Exception) {
                _isProsesSuccess.value = ValidationResult(true, e.message.toString())
            }
        }
    }

}

class AddCartViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCartViewModel::class.java)) {
            return AddCartViewModel(
                cartRepository, productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}