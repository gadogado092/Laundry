package amat.kelolakost.ui.screen.splash

import amat.kelolakost.data.User
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

    fun getKostInit() {
        viewModelScope.launch {
            _stateUser.value = UiState.Loading
            val data = repository.getUser()
            _stateUser.value = UiState.Success(data[0])
        }

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