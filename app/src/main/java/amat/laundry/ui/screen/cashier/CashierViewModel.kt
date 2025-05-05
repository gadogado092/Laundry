package amat.laundry.ui.screen.cashier

import amat.laundry.data.Cashier
import amat.laundry.data.repository.CashierRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CashierViewModel(
    private val cashierRepository: CashierRepository
) : ViewModel() {

    private val _stateCashier: MutableStateFlow<UiState<List<Cashier>>> =
        MutableStateFlow(UiState.Loading)
    val stateCashier: StateFlow<UiState<List<Cashier>>>
        get() = _stateCashier

    fun getCashier() {
        viewModelScope.launch {
            try {
                _stateCashier.value = UiState.Loading
                val data = cashierRepository.getCashier()
                _stateCashier.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCashier.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class CashierViewModelFactory(
    private val cashierRepository: CashierRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashierViewModel::class.java)) {
            return CashierViewModel(
                cashierRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}