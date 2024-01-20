package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.data.CreditDebitHome
import amat.kelolakost.data.repository.CreditDebitRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreditDebitViewModel(
    private val creditDebitRepository: CreditDebitRepository
) : ViewModel() {
    private val _stateListCreditDebit: MutableStateFlow<UiState<List<CreditDebitHome>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCreditDebit: StateFlow<UiState<List<CreditDebitHome>>>
        get() = _stateListCreditDebit

    fun getAllCreditDebit() {
        _stateListCreditDebit.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = creditDebitRepository.getAllCreditDebit()
                _stateListCreditDebit.value = UiState.Success(data)
            }
        } catch (e: Exception) {
            _stateListCreditDebit.value = UiState.Error(e.message.toString())
        }

    }

}

class CreditDebitViewModelFactory(
    private val creditDebitRepository: CreditDebitRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreditDebitViewModel::class.java)) {
            return CreditDebitViewModel(creditDebitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}