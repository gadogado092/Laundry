package amat.laundry.ui.screen.transaction

import amat.laundry.currencyFormatterStringViewZero
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

    private val _stateUiCart: MutableStateFlow<UiState<AddTransactionUi>> =
        MutableStateFlow(UiState.Loading)
    val stateUiCart: StateFlow<UiState<AddTransactionUi>>
        get() = _stateUiCart


    init {
        getCategory()
    }

    private fun getCategory() {
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
                val dataProduct = productRepository.getProductList(
                    categoryId = _categorySelected.value.id
                )
                val dataCart = cartRepository.getCartList(categoryId = _categorySelected.value.id)

                val listData = mutableListOf<ProductCart>()

                dataProduct.forEach { itemProduct ->
                    val productTemp = ProductCart(
                        productId = itemProduct.productId,
                        productName = itemProduct.productName,
                        productPrice = itemProduct.productPrice,
                        categoryName = itemProduct.categoryName,
                        unit = itemProduct.unit,
                        qty = 0F,
                        note = "",
                        productTotalPrice = ""
                    )
                    listData.add(productTemp)
                }

                //ganti value qty dan note jika produk ada dalam cart
                dataCart.forEach { itemCart ->
                    for ((index, itemProduct) in listData.withIndex()) {
                        if (itemCart.productId == itemProduct.productId) {
                            listData[index] =
                                listData[index].copy(qty = itemCart.qty, note = itemCart.note)
                            return@forEach
                        }
                    }
                }

                listData.forEach {
                    Log.d("adada", it.toString())
                }

                _stateListProductCart.value = UiState.Success(listData)
            } catch (e: Exception) {
                _stateListProductCart.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun getTotalService() {
        _stateUiCart.value = UiState.Loading
        viewModelScope.launch {
            try {
                val totalPrice = cartRepository.getTotalPriceCart()
                val totalDataCart = cartRepository.getTotalDataCart()

                if (totalPrice.total == null) {
                    totalPrice.total = "0"
                }

                if (totalDataCart.total == null) {
                    totalDataCart.total = "0"
                }

                _stateUiCart.value = UiState.Success(
                    AddTransactionUi(
                        totalPrice = currencyFormatterStringViewZero(totalPrice.total!!),
                        totalService = totalDataCart.total!!
                    )
                )

            } catch (e: Exception) {
                _stateUiCart.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }


    fun delete(productId: String) {
        viewModelScope.launch {
            cartRepository.delete(productId)
            getProduct()
            getTotalService()
        }
    }

    fun deleteAllCart() {
        viewModelScope.launch {
            cartRepository.deleteAllCart()
            getProduct()
            getTotalService()
        }
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

