package amat.laundry.ui.screen.cashier

import amat.laundry.data.Cashier
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CashierRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCashierViewModel(
    private val cashierRepository: CashierRepository
) : ViewModel() {

    private val _stateCashier: MutableStateFlow<UiState<Cashier>> =
        MutableStateFlow(UiState.Loading)
    val stateCashier: StateFlow<UiState<Cashier>>
        get() = _stateCashier

    private val _stateUi: MutableStateFlow<Cashier> =
        MutableStateFlow(
            Cashier(
                id = "",
                name = "",
                note = "",
                isLastUsed = false,
                isDelete = false
            )
        )
    val stateUi: StateFlow<Cashier>
        get() = _stateUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    fun getCashier(id: String) {
        if (id.isNotEmpty()) {
            clearError()
            viewModelScope.launch {
                try {
                    _stateCashier.value = UiState.Loading
                    val data = cashierRepository.getCashier(id)
                    _stateUi.value = data
                    _stateCashier.value = UiState.Success(data)
                } catch (e: Exception) {
                    _stateCashier.value = UiState.Error(e.message.toString())
                }
            }
        } else {
            _stateCashier.value = UiState.Success(stateUi.value)
        }
    }

    fun setName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
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
                    cashierRepository.update(stateUi.value)
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val id = UUID.randomUUID().toString()
                    cashierRepository.insert(stateUi.value.copy(id = id))
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
                cashierRepository.deleteCashier(id = stateUi.value.id)
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

class AddCashierViewModelFactory(
    private val cashierRepository: CashierRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCashierViewModel::class.java)) {
            return AddCashierViewModel(
                cashierRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}