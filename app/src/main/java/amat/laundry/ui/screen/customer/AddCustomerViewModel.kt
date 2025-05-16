package amat.laundry.ui.screen.customer

import amat.laundry.data.Customer
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CustomerRepository
import amat.laundry.isNumberPhoneValid
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCustomerViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _stateCustomer: MutableStateFlow<UiState<Customer>> =
        MutableStateFlow(UiState.Loading)
    val stateCustomer: StateFlow<UiState<Customer>>
        get() = _stateCustomer

    private val _stateUi: MutableStateFlow<Customer> =
        MutableStateFlow(
            Customer(
                id = "",
                name = "",
                phoneNumber = "",
                note = "",
                isDelete = false
            )
        )
    val stateUi: StateFlow<Customer>
        get() = _stateUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isNumberPhoneValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    fun getCustomer(id: String) {
        if (id.isNotEmpty()) {
            clearError()
            viewModelScope.launch {
                try {
                    _stateCustomer.value = UiState.Loading
                    val data = customerRepository.getCustomer(id)
                    _stateUi.value = data
                    _stateCustomer.value = UiState.Success(data)
                } catch (e: Exception) {
                    _stateCustomer.value = UiState.Error(e.message.toString())
                }
            }
        } else {
            _stateCustomer.value = UiState.Success(stateUi.value)
        }
    }

    fun setName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else if (stateUi.value.name.trim().length < 3) {
            _isNameValid.value = ValidationResult(true, "Nama Minimal 3 Karakter")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(phoneNumber = value)

        if (_stateUi.value.phoneNumber.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(_stateUi.value.phoneNumber.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        } else {
            _isNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(note = value)
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return false
        }

        if (stateUi.value.name.trim().length < 3) {
            _isNameValid.value = ValidationResult(true, "Nama Minimal Karakter")
            _isProsesFailed.value = ValidationResult(true, "Nama Minimal Karakter")
            return false
        }

        if (_stateUi.value.phoneNumber.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isProsesFailed.value = ValidationResult(true, "Nomor Harus Terisi")
            return false
        }
        if (!isNumberPhoneValid(_stateUi.value.phoneNumber.trim())) {
            _isNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isProsesFailed.value = ValidationResult(true, "Nomor Belum Benar")
            return false
        }


        return true
    }

    fun dataDeleteIsComplete(): Boolean {
        if (stateUi.value.id.isEmpty()) {
            _isProsesDeleteFailed.value = ValidationResult(true, "Id Data Tidak Ada")
            return false
        }
        return true
    }

    fun process() {
        try {
            viewModelScope.launch {
                if (stateUi.value.id.isNotEmpty()) {
                    customerRepository.update(stateUi.value)
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val id = UUID.randomUUID().toString()
                    customerRepository.insert(stateUi.value.copy(id = id))
                    _isProsesFailed.value = ValidationResult(false)
                }
            }
        } catch (e: Exception) {
            Log.e("bossku", e.message.toString())
            _isProsesFailed.value = ValidationResult(true, e.message.toString())
        }

    }

    fun processDelete() {
        viewModelScope.launch {
            try {
                customerRepository.deleteCustomer(id = stateUi.value.id)
                _isProsesDeleteFailed.value = ValidationResult(false)
            } catch (e: Exception) {
                Log.e("bossku", e.message.toString())
                _isProsesDeleteFailed.value = ValidationResult(true, e.message.toString())
            }
        }
    }


    private fun clearError() {
        _isProsesDeleteFailed.value = ValidationResult(true, "")
        _isProsesFailed.value = ValidationResult(true, "")
    }

}

class AddCustomerViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCustomerViewModel::class.java)) {
            return AddCustomerViewModel(
                customerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}