package amat.laundrysederhana.ui.screen.category

import amat.laundrysederhana.data.Category
import amat.laundrysederhana.data.repository.CategoryRepository
import amat.laundrysederhana.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _stateCategory: MutableStateFlow<UiState<List<Category>>> =
        MutableStateFlow(UiState.Loading)
    val stateCategory: StateFlow<UiState<List<Category>>>
        get() = _stateCategory

    fun getCategory() {
        viewModelScope.launch {
            try {
                _stateCategory.value = UiState.Loading
                val data = categoryRepository.getCategory()
                _stateCategory.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCategory.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class CategoryViewModelFactory(
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(
                categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}