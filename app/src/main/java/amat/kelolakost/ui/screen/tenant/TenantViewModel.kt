package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TenantViewModel(private val repository: TenantRepository) : ViewModel() {
    private val _stateListTenant: MutableStateFlow<UiState<List<Tenant>>> =
        MutableStateFlow(UiState.Loading)
    val stateListTenant: StateFlow<UiState<List<Tenant>>>
        get() = _stateListTenant

    init {
        getAllTenantInit()
    }

    fun getAllTenantInit() {
        viewModelScope.launch {
            _stateListTenant.value = UiState.Loading
            repository.getAllUnitType()
                .catch {
                    _stateListTenant.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListTenant.value = UiState.Success(data)
                }
        }

    }

}

class TenantViewModelFactory(private val repository: TenantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TenantViewModel::class.java)) {
            return TenantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}