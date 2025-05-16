package amat.laundry.ui.screen.customer

import amat.laundry.data.Customer
import amat.laundry.data.repository.CustomerRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchCustomerViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _stateCustomer: MutableStateFlow<UiState<List<Customer>>> =
        MutableStateFlow(UiState.Loading)
    val stateCustomer: StateFlow<UiState<List<Customer>>>
        get() = _stateCustomer

    private val _searchValue: MutableStateFlow<String> =
        MutableStateFlow("")
    val searchValue: StateFlow<String>
        get() = _searchValue

    fun setSearch(value: String) {
        _searchValue.value = value

        viewModelScope.launch {
            _stateCustomer.value = UiState.Loading
            _searchValue.debounce(500).collectLatest { input ->
                if (input.isNullOrBlank()) {
                    getCustomer()
                    return@collectLatest
                }

                if (input.length < 3) {
                    _stateCustomer.value = UiState.Error("Masukkan Minimal 3 Karakter")
                    return@collectLatest
                }

                searchCustomer(input)
            }
        }

    }

    fun searchCustomer(value: String) {
        viewModelScope.launch {
            try {
                _stateCustomer.value = UiState.Loading
                val data = customerRepository.searchCustomer(value)

                _stateCustomer.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCustomer.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getCustomer() {
        viewModelScope.launch {
            try {
                _stateCustomer.value = UiState.Loading
                val data = customerRepository.getCustomer()
                _stateCustomer.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCustomer.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class SearchCustomerViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchCustomerViewModel::class.java)) {
            return SearchCustomerViewModel(
                customerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}