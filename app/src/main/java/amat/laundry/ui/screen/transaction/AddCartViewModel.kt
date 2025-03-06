package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cart
import amat.laundry.data.repository.CartRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddCartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {



    fun insertCart(productId: String) {
        viewModelScope.launch {
            cartRepository.insert(Cart(productId, 1F, ""))
        }
    }

}

class AddCartViewModelFactory(
    private val cartRepository: CartRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCartViewModel::class.java)) {
            return AddCartViewModel(
                cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}