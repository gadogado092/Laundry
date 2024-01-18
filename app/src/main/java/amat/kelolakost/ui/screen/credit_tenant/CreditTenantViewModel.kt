package amat.kelolakost.ui.screen.credit_tenant

import amat.kelolakost.data.CreditTenantHome
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreditTenantViewModel(private val repository: CreditTenantRepository) : ViewModel() {
    private val _stateListCreditTenant: MutableStateFlow<UiState<List<CreditTenantHome>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCreditTenant: StateFlow<UiState<List<CreditTenantHome>>>
        get() = _stateListCreditTenant

    fun getAllCreditTenant() {
        _stateListCreditTenant.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getAllCreditTenant()
                _stateListCreditTenant.value= UiState.Success(data)
            }
        }catch (e:Exception){
            _stateListCreditTenant.value = UiState.Error(e.message.toString())
        }

    }

}

class CreditTenantViewModelFactory(private val repository: CreditTenantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreditTenantViewModel::class.java)) {
            return CreditTenantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}