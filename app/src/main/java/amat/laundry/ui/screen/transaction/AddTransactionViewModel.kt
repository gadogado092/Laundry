package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cart
import amat.laundry.data.Category
import amat.laundry.data.ProductCart
import amat.laundry.data.entity.FilterEntity
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
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _stateListCategory: MutableStateFlow<UiState<List<Category>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCategory: StateFlow<UiState<List<Category>>>
        get() = _stateListCategory

    private val _categorySelected = MutableStateFlow(Category("", "", "", false))
    val categorySelected: StateFlow<Category>
        get() = _categorySelected


    init {
        getCategory()
    }

    private fun getCategory() {
        Log.d("saya", "get all kost")
        viewModelScope.launch {
            _stateListCategory.value = UiState.Loading
//            val data = kostRepository.getKost()
//            _stateListKost.value = UiState.Success(data)
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

    private fun getProduct() {
//        _stateListUnit.value = UiState.Loading
//        viewModelScope.launch {
//            try {
//                val data = unitRepository.getUnitHome(
//                    unitStatusId = _statusSelected.value.value,
//                    kostId = _kostSelected.value.id
//                )
//                _stateListUnit.value = UiState.Success(data)
//            } catch (e: Exception) {
//                _stateListUnit.value = UiState.Error("Error ${e.message.toString()}")
//            }
//        }
    }

    fun deleteCart(productId: String){
        TODO("Not yet implemented")
    }

    fun deleteAllCart() {
        TODO("Not yet implemented")
    }
}


class AddTransactionViewModelFactory(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            return AddTransactionViewModel(
                productRepository, categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}

