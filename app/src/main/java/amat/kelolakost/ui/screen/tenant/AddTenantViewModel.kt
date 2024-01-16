package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.generateDateNow
import amat.kelolakost.isEmailValid
import amat.kelolakost.isNumberPhoneValid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddTenantViewModel(private val tenantRepository: TenantRepository) : ViewModel() {
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

    private val _isInsertSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isInsertSuccess: StateFlow<ValidationResult>
        get() = _isInsertSuccess

    fun setName(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(name = value)
        if (_tenantUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
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
        _isInsertSuccess.value = ValidationResult(true, "")
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
        _isInsertSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(address = value)
        if (_tenantUi.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
        } else {
            _isAddressValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
        _tenantUi.value = _tenantUi.value.copy(note = value)
    }

    fun prosesInsert() {
        _isInsertSuccess.value = ValidationResult(true, "")
        if (_tenantUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isInsertSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }

        if (_tenantUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isInsertSuccess.value = ValidationResult(true, "Nomor Harus Terisi")
            return
        } else if (!isNumberPhoneValid(_tenantUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isInsertSuccess.value = ValidationResult(true, "Nomor Belum Benar")
            return
        }

        if (_tenantUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
            _isInsertSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
            return
        } else if (!isEmailValid(_tenantUi.value.email.trim())) {
            _isEmailValid.value = ValidationResult(true, "Email Belum Valid")
            _isInsertSuccess.value = ValidationResult(true, "Email Belum Valid")
            return
        }

        if (_tenantUi.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
            _isInsertSuccess.value = ValidationResult(true, "Alamat Tidak Boleh Kosong")
            return
        }

            viewModelScope.launch {
                val id = UUID.randomUUID()
                val tenant = Tenant(
                    id = id.toString(),
                    name = _tenantUi.value.name,
                    note = _tenantUi.value.note,
                    gender = _tenantUi.value.gender,
                    numberPhone = _tenantUi.value.numberPhone,
                    address = _tenantUi.value.address,
                    email = _tenantUi.value.email,
                    limitCheckOut = "",
                    unitId = "0",
                    guaranteeCost = 0,
                    additionalCost = 0,
                    noteAdditionalCost = "",
                    createAt = generateDateNow(),
                    isDelete = false
                )
                insertTenant(tenant)
            }

    }

    private suspend fun insertTenant(tenant: Tenant) {
        try {
            tenantRepository.insertTenant(tenant)
            _isInsertSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isInsertSuccess.value = ValidationResult(true, "Gagagl Insert " + e.message.toString())
        }
    }
}

class AddTenantViewModelFactory(
    private val tenantRepository: TenantRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTenantViewModel::class.java)) {
            return AddTenantViewModel(tenantRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}