package amat.kelolakost.ui.screen.other

import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtherViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stateUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateUser: StateFlow<UiState<User>>
        get() = _stateUser

    private val _typeWa: MutableStateFlow<String> =
        MutableStateFlow("")
    val typeWa: StateFlow<String>
        get() = _typeWa

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun getKostInit() {
        clearError()
        viewModelScope.launch {
            _stateUser.value = UiState.Loading
            val data = repository.getUser()
            _stateUser.value = UiState.Success(data[0])
            _typeWa.value = data[0].typeWa
        }

    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class OtherViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherViewModel::class.java)) {
            return OtherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}