package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryCreditDebitViewModel(private val repository: CashFlowRepository) : ViewModel() {
    private val _stateListCashFlow: MutableStateFlow<UiState<List<CashFlow>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCashFlow: StateFlow<UiState<List<CashFlow>>>
        get() = _stateListCashFlow

    fun getCreditDebitHistory(creditDebitId: String) {
        _stateListCashFlow.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getCreditDebitHistory(creditDebitId = creditDebitId)
                _stateListCashFlow.value = UiState.Success(data)
            }
        } catch (e: Exception) {
            _stateListCashFlow.value = UiState.Error(e.message.toString())
        }

    }

}

class HistoryCreditDebitViewModelFactory(private val repository: CashFlowRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryCreditDebitViewModel::class.java)) {
            return HistoryCreditDebitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}