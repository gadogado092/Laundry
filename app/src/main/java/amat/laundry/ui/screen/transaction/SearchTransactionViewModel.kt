package amat.laundry.ui.screen.transaction

import amat.laundry.data.TransactionCustomer
import amat.laundry.data.User
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", 32, 32, "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _stateTransaction: MutableStateFlow<UiState<List<TransactionCustomer>>> =
        MutableStateFlow(UiState.Loading)
    val stateTransaction: StateFlow<UiState<List<TransactionCustomer>>>
        get() = _stateTransaction

    private val _searchValue: MutableStateFlow<String> =
        MutableStateFlow("")
    val searchValue: StateFlow<String>
        get() = _searchValue

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            userRepository.getDetail()
                .catch {

                }
                .collect { data ->
                    _user.value = data
                }
        }
    }

    fun setSearch(value: String) {
        _searchValue.value = value

        viewModelScope.launch {
            _searchValue.debounce(500).collectLatest { input ->
                _stateTransaction.value = UiState.Loading
                if (input.isNullOrBlank()) {
                    val listEmpty = mutableListOf<TransactionCustomer>()
                    _stateTransaction.value = UiState.Success(listEmpty)
                    return@collectLatest
                }

                if (input.length < 5) {
                    _stateTransaction.value = UiState.Error("Masukkan Minimal 5 Karakter")
                    return@collectLatest
                }

                searchTransaction(input)
            }
        }

    }

    fun searchTransaction(value: String) {
        viewModelScope.launch {
            try {
                _stateTransaction.value = UiState.Loading
                val data = transactionRepository.searchTransaction(value)

                _stateTransaction.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateTransaction.value = UiState.Error(e.message.toString())
            }
        }
    }


}

class SearchTransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchTransactionViewModel::class.java)) {
            return SearchTransactionViewModel(
                transactionRepository, userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}