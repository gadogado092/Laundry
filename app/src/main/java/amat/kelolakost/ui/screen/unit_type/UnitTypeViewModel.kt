package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.data.UnitType
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UnitTypeViewModel(private val repository: UnitTypeRepository) : ViewModel() {
    private val _stateListUnitType: MutableStateFlow<UiState<List<UnitType>>> =
        MutableStateFlow(UiState.Loading)
    val stateListUnitType: StateFlow<UiState<List<UnitType>>>
        get() = _stateListUnitType

    init {
        getAllUnitTypeInit()
    }

    fun getAllUnitTypeInit() {
        viewModelScope.launch {
            _stateListUnitType.value = UiState.Loading
            repository.getAllUnitType()
                .catch {
                    _stateListUnitType.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListUnitType.value = UiState.Success(data)
                }
        }

    }

}

class UnitTypeViewModelFactory(private val repository: UnitTypeRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitTypeViewModel::class.java)) {
            return UnitTypeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}