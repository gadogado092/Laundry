package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cart
import amat.laundry.data.Category
import amat.laundry.data.ProductCart
import amat.laundry.data.repository.CartRepository
import amat.laundry.data.repository.CategoryRepository
import amat.laundry.data.repository.ProductRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _stateListCategory: MutableStateFlow<UiState<List<Category>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCategory: StateFlow<UiState<List<Category>>>
        get() = _stateListCategory

    private val _categorySelected = MutableStateFlow(Category("", "", "", false))
    val categorySelected: StateFlow<Category>
        get() = _categorySelected

    private val _stateListProductCart: MutableStateFlow<UiState<List<ProductCart>>> =
        MutableStateFlow(UiState.Loading)
    val stateListProductCart: StateFlow<UiState<List<ProductCart>>>
        get() = _stateListProductCart


    init {
        getCategory()
    }

    private fun getCategory() {
        Log.d("saya", "get all kost")
        viewModelScope.launch {
            _stateListCategory.value = UiState.Loading
            categoryRepository.getAllCategory()
                .catch {
                    _stateListCategory.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListCategory.value = UiState.Success(data)

                    if (_categorySelected.value.id == "") {
                        updateCategorySelected(data[0])
                    }

                }


        }
    }

    fun updateCategorySelected(category: Category) {
        _categorySelected.value = category
        getProduct()
    }

    fun getProduct() {
        _stateListProductCart.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = productRepository.getProductCartList(
                    categoryId = _categorySelected.value.id
                )
                _stateListProductCart.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListProductCart.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun insertCart(productId: String) {
        viewModelScope.launch {
            cartRepository.insert(Cart(productId, 1F, ""))
            getProduct()
        }
    }

    fun delete(productId: String) {
        viewModelScope.launch {
            cartRepository.delete(Cart(productId, 1F, ""))
            getProduct()
        }
    }

    fun deleteAllCart() {
        viewModelScope.launch {
            cartRepository.deleteAllCart()
            getProduct()
        }
    }

    fun deleteCart(productId: String) {
        TODO("Not yet implemented")
    }

}


class AddTransactionViewModelFactory(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            return AddTransactionViewModel(
                productRepository, categoryRepository, cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}

