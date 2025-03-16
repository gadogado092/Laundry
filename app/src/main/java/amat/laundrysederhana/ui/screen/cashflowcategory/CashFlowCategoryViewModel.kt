package amat.laundrysederhana.ui.screen.cashflowcategory

import amat.laundrysederhana.data.CashFlowCategory
import amat.laundrysederhana.data.repository.CashFlowCategoryRepository
import amat.laundrysederhana.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CashFlowCategoryViewModel(
    private val cashFlowCategoryRepository: CashFlowCategoryRepository
) : ViewModel() {
    private val _stateCategory: MutableStateFlow<UiState<List<CashFlowCategory>>> =
        MutableStateFlow(UiState.Loading)
    val stateCategory: StateFlow<UiState<List<CashFlowCategory>>>
        get() = _stateCategory

    fun getCategory() {
        viewModelScope.launch {
            try {
                _stateCategory.value = UiState.Loading
                val data = cashFlowCategoryRepository.getCashFlowCategory0()
                _stateCategory.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCategory.value = UiState.Error(e.message.toString())
            }
        }
    }
}

class CashFlowCategoryViewModelFactory(
    private val cashFlowCategoryRepository: CashFlowCategoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashFlowCategoryViewModel::class.java)) {
            return CashFlowCategoryViewModel(
                cashFlowCategoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}