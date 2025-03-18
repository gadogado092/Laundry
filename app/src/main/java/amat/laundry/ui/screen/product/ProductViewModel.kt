package amat.laundry.ui.screen.product

import amat.laundry.data.ProductCategory
import amat.laundry.data.repository.ProductRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _stateProduct: MutableStateFlow<UiState<List<ProductCategory>>> =
        MutableStateFlow(UiState.Loading)
    val stateProduct: StateFlow<UiState<List<ProductCategory>>>
        get() = _stateProduct

    fun getProduct() {
        viewModelScope.launch {
            try {
                _stateProduct.value = UiState.Loading
                val data = productRepository.getProductList()
                _stateProduct.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class ProductViewModelFactory(
    private val productRepository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(
                productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}