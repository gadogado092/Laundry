package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.isEmailValid
import amat.kelolakost.isNumberPhoneValid
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UpdateTenantViewModel(private val tenantRepository: TenantRepository) : ViewModel() {

    private val _stateTenantUi: MutableStateFlow<UiState<Tenant>> =
        MutableStateFlow(UiState.Loading)
    val stateTenantUi: StateFlow<UiState<Tenant>>
        get() = _stateTenantUi

    private val _tenantUi: MutableStateFlow<TenantUi> =
        MutableStateFlow(TenantUi())
    val tenantUi: StateFlow<TenantUi>
        get() = _tenantUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isNumberPhoneValid

    private val _isEmailValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isEmailValid: StateFlow<ValidationResult>
        get() = _isEmailValid

    private val _isAddressValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isAddressValid: StateFlow<ValidationResult>
        get() = _isAddressValid

    private val _isUpdateSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isUpdateSuccess: StateFlow<ValidationResult>
        get() = _isUpdateSuccess

    fun getDetail(id: String) {
        viewModelScope.launch {
            _stateTenantUi.value = UiState.Loading
            tenantRepository.getDetail(id)
                .catch {
                    _stateTenantUi.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateTenantUi.value = UiState.Success(data)
                    _tenantUi.value =
                        TenantUi(
                            id = data.id,
                            name = data.name,
                            email = data.email,
                            numberPhone = data.numberPhone,
                            gender = data.gender,
                            address = data.address,
                            note = data.note,
                            limitCheckOut = data.limitCheckOut,
                            unitId = data.unitId,
                            guaranteeCost = data.guaranteeCost,
                            additionalCost = data.additionalCost,
                            noteAdditionalCost = data.noteAdditionalCost,
                            isDelete = data.isDelete
                        )
                }
        }

    }

    fun setName(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(name = value)
        if (_tenantUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(numberPhone = value)
        if (_tenantUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(_tenantUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        } else {
            _isNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun setEmail(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(email = value)

        if (_tenantUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
        } else if (!isEmailValid(_tenantUi.value.email.trim())) {
            _isEmailValid.value = ValidationResult(true, "Email Belum Valid")
        } else {
            _isEmailValid.value = ValidationResult(false, "")
        }
    }

    fun setGender(value: Boolean) {
        _tenantUi.value = _tenantUi.value.copy(gender = value)
    }

    fun setAddress(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(address = value)
        if (_tenantUi.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
        } else {
            _isAddressValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(note = value)
    }

    fun prosesUpdate() {
        _isUpdateSuccess.value = ValidationResult(true, "")
        if (_tenantUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isUpdateSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }

        if (_tenantUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isUpdateSuccess.value = ValidationResult(true, "Nomor Harus Terisi")
            return
        } else if (!isNumberPhoneValid(_tenantUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isUpdateSuccess.value = ValidationResult(true, "Nomor Belum Benar")
            return
        }

        if (_tenantUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
            _isUpdateSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
            return
        } else if (!isEmailValid(_tenantUi.value.email.trim())) {
            _isEmailValid.value = ValidationResult(true, "Email Belum Valid")
            _isUpdateSuccess.value = ValidationResult(true, "Email Belum Valid")
            return
        }

        if (_tenantUi.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
            _isUpdateSuccess.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
            return
        }

        viewModelScope.launch {
            val tenant = Tenant(
                id = _tenantUi.value.id,
                name = _tenantUi.value.name,
                note = _tenantUi.value.note,
                gender = _tenantUi.value.gender,
                numberPhone = _tenantUi.value.numberPhone,
                address = _tenantUi.value.address,
                email = _tenantUi.value.email,
                limitCheckOut = _tenantUi.value.limitCheckOut,
                unitId = _tenantUi.value.unitId,
                guaranteeCost = _tenantUi.value.guaranteeCost,
                additionalCost = _tenantUi.value.additionalCost,
                noteAdditionalCost = _tenantUi.value.noteAdditionalCost,
                isDelete = _tenantUi.value.isDelete
            )
            updateTenant(tenant)
        }

    }

    private suspend fun updateTenant(tenant: Tenant) {
        try {
            tenantRepository.updateTenant(tenant)
            _isUpdateSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isUpdateSuccess.value = ValidationResult(true, "Gagagl Insert " + e.message.toString())
        }
    }
}

class UpdateTenantViewModelFactory(
    private val tenantRepository: TenantRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateTenantViewModel::class.java)) {
            return UpdateTenantViewModel(tenantRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}