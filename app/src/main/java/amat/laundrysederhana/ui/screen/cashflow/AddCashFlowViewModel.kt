package amat.laundrysederhana.ui.screen.cashflow

import amat.laundrysederhana.data.CashFlow
import amat.laundrysederhana.data.CashFlowAndCategory
import amat.laundrysederhana.data.CashFlowCategory
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.CashFlowCategoryRepository
import amat.laundrysederhana.data.repository.CashFlowRepository
import amat.laundrysederhana.generateDateTimeNow
import amat.laundrysederhana.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCashFlowViewModel(
    private val cashFlowRepository: CashFlowRepository,
    private val cashFlowCategoryRepository: CashFlowCategoryRepository
) : ViewModel() {

    private val _stateCashFlow: MutableStateFlow<UiState<CashFlowAndCategory>> =
        MutableStateFlow(UiState.Loading)
    val stateCashFlow: StateFlow<UiState<CashFlowAndCategory>>
        get() = _stateCashFlow

    private val _stateUi: MutableStateFlow<CashFlowAndCategory> =
        MutableStateFlow(
            CashFlowAndCategory(
                cashFlowId = "",
                note = "",
                nominal = "",
                createAt = "",
                cashFlowCategoryId = "0",
                cashFlowCategoryName = "Tanpa Kategori"
            )
        )
    val stateUi: StateFlow<CashFlowAndCategory>
        get() = _stateUi

    private val _stateListCategory: MutableStateFlow<UiState<List<CashFlowCategory>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListCategory: StateFlow<UiState<List<CashFlowCategory>>>
        get() = _stateListCategory

    private val _isNominalValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNominalValid: StateFlow<ValidationResult>
        get() = _isNominalValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    fun getCashFlow(id: String) {
        if (id.isNotEmpty()) {
            clearError()
            viewModelScope.launch {
                try {
                    _stateCashFlow.value = UiState.Loading
                    val data = cashFlowRepository.getCashFlow(id)
                    _stateUi.value = data
                    _stateCashFlow.value = UiState.Success(data)
                } catch (e: Exception) {
                    _stateCashFlow.value = UiState.Error(e.message.toString())
                }
            }
        } else {
            _stateCashFlow.value = UiState.Success(stateUi.value)
        }
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(note = value)
    }

    fun setNominal(value: String) {
        clearError()
        val cleanValue = value.trim().replace(" ", "")
        if (cleanValue.toIntOrNull() != null) {
            _stateUi.value = stateUi.value.copy(nominal = cleanValue)
            if (cleanValue.isEmpty() || cleanValue.toInt() < 1) {
                _isNominalValid.value =
                    ValidationResult(true, "Nominal Tidak Boleh Kosong")
            } else {
                _isNominalValid.value = ValidationResult(false, "")
            }
        } else {
            if (cleanValue.isEmpty()) {
                _isNominalValid.value =
                    ValidationResult(true, "Nominal Tidak Boleh Kosong")
                _stateUi.value = stateUi.value.copy(nominal = "")
            } else {
                _isNominalValid.value =
                    ValidationResult(true, "Masukkan Format Angka Yang Sesuai")
                _stateUi.value = stateUi.value.copy(nominal = "")
            }
        }
    }

    fun getCategory() {
        clearError()
        _stateListCategory.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashFlowCategoryRepository.getCategory()
                _stateListCategory.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListCategory.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setCategorySelected(id: String, name: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(cashFlowCategoryId = id, cashFlowCategoryName = name)
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (stateUi.value.nominal.trim().isEmpty()) {
            _isNominalValid.value = ValidationResult(true, "Nominal Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nominal Tidak Boleh Kosong")
            return false
        }

        return true
    }

    fun dataDeleteIsComplete(): Boolean {
        if (stateUi.value.cashFlowId.isEmpty()) {
            _isProsesDeleteFailed.value = ValidationResult(true, "Id Data Tidak Ada")
            return false
        }
        return true
    }

    fun process() {
        try {
            viewModelScope.launch {
                val dataCashFlow = stateUi.value
                if (stateUi.value.cashFlowId.isNotEmpty()) {
                    cashFlowRepository.update(
                        CashFlow(
                            id = dataCashFlow.cashFlowId,
                            note = dataCashFlow.note,
                            nominal = dataCashFlow.nominal,
                            cashFlowCategoryId = dataCashFlow.cashFlowCategoryId,
                            type = 1,
                            createAt = dataCashFlow.createAt,
                            isDelete = false
                        )
                    )
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val id = UUID.randomUUID().toString()
                    val createAt = generateDateTimeNow()
                    cashFlowRepository.insert(
                        CashFlow(
                            id = id,
                            note = dataCashFlow.note,
                            nominal = dataCashFlow.nominal,
                            cashFlowCategoryId = dataCashFlow.cashFlowCategoryId,
                            type = 1,
                            createAt = createAt,
                            isDelete = false
                        )
                    )
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
                cashFlowRepository.deleteCashFlow(id = stateUi.value.cashFlowId)
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
        _stateListCategory.value = UiState.Error("")
    }

}

class AddCashFlowViewModelFactory(
    private val cashFlowRepository: CashFlowRepository,
    private val cashFlowCategoryRepository: CashFlowCategoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCashFlowViewModel::class.java)) {
            return AddCashFlowViewModel(
                cashFlowRepository, cashFlowCategoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}