package amat.kelolakost.ui.screen.credit_tenant

import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CreditTenantHome
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailCreditTenantViewModel(private val repository: CreditTenantRepository) : ViewModel() {
    private val _stateCreditTenant: MutableStateFlow<UiState<CreditTenantHome>> =
        MutableStateFlow(UiState.Loading)
    val stateCreditTenant: StateFlow<UiState<CreditTenantHome>>
        get() = _stateCreditTenant

    private val _stateListCreditTenant: MutableStateFlow<UiState<List<CreditTenant>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCreditTenant: StateFlow<UiState<List<CreditTenant>>>
        get() = _stateListCreditTenant

    private val _isProsesDeleteSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteSuccess: StateFlow<ValidationResult>
        get() = _isProsesDeleteSuccess

    fun getCreditTenant(tenantId: String) {
        _stateCreditTenant.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getCreditTenant(tenantId)
                _stateCreditTenant.value = UiState.Success(data)
            }
        } catch (e: Exception) {
            _stateCreditTenant.value = UiState.Error(e.message.toString())
        }

    }

    fun getAllCreditTenant(tenantId: String) {
        _stateListCreditTenant.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getAllCreditTenant(tenantId)
                _stateListCreditTenant.value = UiState.Success(data)
            }
        } catch (e: Exception) {
            _stateListCreditTenant.value = UiState.Error(e.message.toString())
        }

    }

    fun delete(creditTenantId: String) {
        _isProsesDeleteSuccess.value = ValidationResult(true, "")
        try {
            viewModelScope.launch {
                repository.deleteCreditTenant(creditTenantId)
                _isProsesDeleteSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesDeleteSuccess.value = ValidationResult(true, e.message.toString())
        }
    }

}

class DetailCreditTenantViewModelFactory(private val repository: CreditTenantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailCreditTenantViewModel::class.java)) {
            return DetailCreditTenantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}