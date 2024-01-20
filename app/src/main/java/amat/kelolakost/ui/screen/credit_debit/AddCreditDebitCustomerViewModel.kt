package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CustomerCreditDebitRepository
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

class AddCreditDebitCustomerViewModel(
    private val repositoryCustomer: CustomerCreditDebitRepository
) : ViewModel() {
    private val _stateUi: MutableStateFlow<AddCreditDebitCustomerUi> =
        MutableStateFlow(
            AddCreditDebitCustomerUi(
            )
        )
    val stateUi: StateFlow<AddCreditDebitCustomerUi>
        get() = _stateUi

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

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    init {
        _stateUi.value = stateUi.value.copy(createAt = generateDateNow())
    }

    fun setName(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(name = value)
        if (_stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(numberPhone = value)
        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(stateUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        } else {
            _isNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun setEmail(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(email = value)

        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
        } else if (!isEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationResult(true, "Email Belum Valid")
        } else {
            _isEmailValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    fun prosesInsert() {
        clearError()

        if (_stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isProsesSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }
        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Harus Terisi")
            return
        } else if (!isNumberPhoneValid(stateUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Belum Benar")
            return
        }

        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
            _isProsesSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
            return
        } else if (!isEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationResult(true, "Email Belum Valid")
            _isProsesSuccess.value = ValidationResult(true, "Email Belum Valid")
            return
        }


        val customerCreditDebit = CustomerCreditDebit(
            id = UUID.randomUUID().toString(),
            name = stateUi.value.name,
            numberPhone = stateUi.value.numberPhone,
            note = stateUi.value.note,
            email = stateUi.value.email,
            createAt = stateUi.value.createAt,
            isDelete = false
        )

        viewModelScope.launch {
            insertCustomer(customerCreditDebit)
        }
    }

    private suspend fun insertCustomer(customerCreditDebit: CustomerCreditDebit) {
        try {
            repositoryCustomer.insert(customerCreditDebit)
            _isProsesSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, "Gagagl Insert " + e.message.toString())
        }
    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class AddCreditDebitCustomerViewModelFactory(
    private val repositoryCustomer: CustomerCreditDebitRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCreditDebitCustomerViewModel::class.java)) {
            return AddCreditDebitCustomerViewModel(repositoryCustomer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}