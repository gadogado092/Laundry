package amat.kelolakost.ui.screen.kost

import amat.kelolakost.data.Kost
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class KostViewModel(private val repository: KostRepository) : ViewModel() {
    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Loading)
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    init {
        getAllKostInit()
    }

    fun getAllKostInit() {
        viewModelScope.launch {
            _stateListKost.value = UiState.Loading
            repository.getAllKost()
                .catch {
                    _stateListKost.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListKost.value = UiState.Success(data)
                }
        }

    }

}

class KostViewModelFactory(private val repository: KostRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KostViewModel::class.java)) {
            return KostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}