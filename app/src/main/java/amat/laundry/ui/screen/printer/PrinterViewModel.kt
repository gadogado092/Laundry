package amat.laundry.ui.screen.printer

import amat.laundry.data.User
import amat.laundry.data.repository.CartRepository
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.ui.common.UiState
import amat.laundry.ui.screen.transaction.PaymentViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PrinterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stateInitUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateInitUser: StateFlow<UiState<User>>
        get() = _stateInitUser

    fun getDetail() {
        viewModelScope.launch {
            _stateInitUser.value = UiState.Loading
            userRepository.getDetail()
                .catch {
                    _stateInitUser.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateInitUser.value = UiState.Success(data)
                }
        }
    }

}

class PrinterViewModelFactory(
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrinterViewModel::class.java)) {
            return PrinterViewModel(
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}