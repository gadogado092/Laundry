package amat.laundry.ui.screen.transaction

import amat.laundry.cleanPointZeroFloat
import amat.laundry.data.Cart
import amat.laundry.data.ProductCart
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

    private val _stateProduct: MutableStateFlow<UiState<ProductCart>> =
        MutableStateFlow(UiState.Loading)
    val stateProduct: StateFlow<UiState<ProductCart>>
        get() = _stateProduct

    private val _stateUi: MutableStateFlow<AddCartUi> =
        MutableStateFlow(AddCartUi())
    val stateUi: StateFlow<AddCartUi>
        get() = _stateUi

    private val _isQtyValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isQtyValid: StateFlow<ValidationResult>
        get() = _isQtyValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    fun getDetailProduct(productId: String) {
        viewModelScope.launch {
            _stateProduct.value = UiState.Loading
            try {
                val product = productRepository.getProductCategoryDetail(productId)

                var productTemp = ProductCart(
                    productId = product.productId,
                    productName = product.productName,
                    productPrice = product.productPrice,
                    categoryName = product.categoryName,
                    unit = product.unit,
                    qty = 0F,
                    note = "",
                    productTotalPrice = "",
                    categoryId = product.categoryId
                )

                val cart = cartRepository.getCartDetail(productId)

                if (cart != null) {
                    productTemp = productTemp.copy(qty = cart.qty, note = cart.note)
                }

                val totalPrice = productTemp.qty * productTemp.productPrice

                val qty = cleanPointZeroFloat(productTemp.qty)

                _stateUi.value =
                    stateUi.value.copy(
                        productId = productTemp.productId,
                        qty = qty,
                        note = productTemp.note,
                        price = productTemp.productPrice.toString(),
                        totalPrice = totalPrice.toInt().toString()
                    )

                _stateProduct.value = UiState.Success(productTemp)

            } catch (e: Exception) {
                Log.e("add cart", e.message.toString())
                _stateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setQty(value: String) {
        clearError()

        if (value.isEmpty()) {
            _stateUi.value =
                stateUi.value.copy(qty = "", totalPrice = "0")
            return
        }

        var qty = 0F
        val cleanValue = value.replace(",", ".").replace(" ", "")
        //check qty is float or not
        if (cleanValue.toFloatOrNull() != null) {
            qty = cleanValue.toFloat()
            val totalPrice = stateUi.value.price.toFloat() * qty
            _stateUi.value =
                stateUi.value.copy(qty = value, totalPrice = totalPrice.toInt().toString())
            _isQtyValid.value = ValidationResult(false, "")
        } else {
            _isQtyValid.value = ValidationResult(true, "Masukkan Format Angka Desimal")
            _stateUi.value =
                stateUi.value.copy(qty = "", totalPrice = "0")
        }

    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    private fun clearError() {
        _isProsesFailed.value = ValidationResult(true, "")
    }

    fun insertCart() {
        clearError()

        var qty = 0F
        val cleanValue = stateUi.value.qty.replace(",", ".").replace(" ", "")
        //check qty is float or not
        if (cleanValue.isNotEmpty()) {
            if (cleanValue.toFloatOrNull() != null) {
                qty = cleanValue.toFloat()
            } else {
                _isQtyValid.value = ValidationResult(true, "Masukkan Format Angka Desimal")
                _isProsesFailed.value = ValidationResult(true, "Masukkan Format Angka Desimal")
                return
            }

            if (qty < 0F) {
                _isQtyValid.value = ValidationResult(true, "Angka Qty Harus Positif")
                _isProsesFailed.value = ValidationResult(true, "Angka Qty Harus Positif")
                return
            }

        }


        viewModelScope.launch {
            try {
                if (qty == 0F) {
                    cartRepository.delete(stateUi.value.productId)
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val totalPrice = stateUi.value.price.toFloat() * qty

                    cartRepository.insert(
                        Cart(
                            productId = stateUi.value.productId,
                            qty = stateUi.value.qty.toFloat(),
                            note = stateUi.value.note,
                            totalPrice = totalPrice.toInt().toString()
                        )
                    )
                    _isProsesFailed.value = ValidationResult(false)
                }

            } catch (e: Exception) {
                _isProsesFailed.value = ValidationResult(true, e.message.toString())
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