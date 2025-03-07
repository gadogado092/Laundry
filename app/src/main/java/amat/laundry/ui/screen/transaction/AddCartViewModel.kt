package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cart
import amat.laundry.data.ProductCart
import amat.laundry.data.User
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
                        qty = if (productTemp.qty == 0F) "" else productTemp.qty.toString(),
                        note = productTemp.note,
                        totalPrice = totalPrice.toString()
                    )

                _stateInitProduct.value = UiState.Success(productTemp)

            } catch (e: Exception) {
                Log.e("add cart", e.message.toString())
                _stateInitProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun insertCart(cart: Cart) {
        viewModelScope.launch {
            cartRepository.insert(cart)
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