package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UpdateUnitTypeViewModel(private val unitTypeRepository: UnitTypeRepository) : ViewModel() {

    private val _stateInitUnitType: MutableStateFlow<UiState<UnitType>> =
        MutableStateFlow(UiState.Loading)
    val stateInitUnitType: StateFlow<UiState<UnitType>>
        get() = _stateInitUnitType

    private val _unitTypeUi: MutableStateFlow<UnitTypeUi> =
        MutableStateFlow(UnitTypeUi())
    val unitTypeUi: StateFlow<UnitTypeUi>
        get() = _unitTypeUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isUpdateSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isUpdateSuccess: StateFlow<ValidationResult>
        get() = _isUpdateSuccess

    fun setName(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(name = value)
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setPriceGuarantee(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = valueFormat)
        }
    }

    fun setPriceDay(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = valueFormat)
        }
    }

    fun setPriceWeek(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = valueFormat)
        }
    }

    fun setPriceMonth(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = valueFormat)
        }
    }

    fun setPriceThreeMonth(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = valueFormat)
        }
    }

    fun setPriceSixMonth(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = valueFormat)
        }
    }

    fun setPriceYear(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = valueFormat)
        }
    }

    fun setNote(value: String) {
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(note = value)
    }

    fun getDetail(id: String) {
        viewModelScope.launch {
            _stateInitUnitType.value = UiState.Loading
            unitTypeRepository.getDetail(id)
                .catch {
                    _stateInitUnitType.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateInitUnitType.value = UiState.Success(data)
                    _unitTypeUi.value =
                        UnitTypeUi(
                            id = data.id,
                            name = data.name,
                            note = data.note,
                            priceDay = currencyFormatterString(data.priceDay.toString()),
                            priceWeek = currencyFormatterString(data.priceWeek.toString()),
                            priceMonth = currencyFormatterString(data.priceMonth.toString()),
                            priceThreeMonth = currencyFormatterString(data.priceThreeMonth.toString()),
                            priceSixMonth = currencyFormatterString(data.priceSixMonth.toString()),
                            priceYear = currencyFormatterString(data.priceYear.toString()),
                            priceGuarantee = currencyFormatterString(data.priceGuarantee.toString())
                        )
                }
        }
    }

    fun prosesUpdate() {
        _isUpdateSuccess.value = ValidationResult(true, "")
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isUpdateSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }

        if (_unitTypeUi.value.priceDay.trim().isEmpty()
            && _unitTypeUi.value.priceWeek.trim().isEmpty()
            && _unitTypeUi.value.priceMonth.trim().isEmpty()
            && _unitTypeUi.value.priceThreeMonth.trim().isEmpty()
            && _unitTypeUi.value.priceSixMonth.trim().isEmpty()
            && _unitTypeUi.value.priceYear.trim().isEmpty()

            && _unitTypeUi.value.priceDay.trim() == "0"
            && _unitTypeUi.value.priceWeek.trim() == "0"
            && _unitTypeUi.value.priceMonth.trim() == "0"
            && _unitTypeUi.value.priceThreeMonth.trim() == "0"
            && _unitTypeUi.value.priceSixMonth.trim() == "0"
            && _unitTypeUi.value.priceYear.trim() == "0"
        ) {
            _isUpdateSuccess.value = ValidationResult(true, "Masukkan minimal 1 Harga")
            return
        }

        if (_unitTypeUi.value.priceDay.trim() == "0"
            && _unitTypeUi.value.priceWeek.trim() == "0"
            && _unitTypeUi.value.priceMonth.trim() == "0"
            && _unitTypeUi.value.priceThreeMonth.trim() == "0"
            && _unitTypeUi.value.priceSixMonth.trim() == "0"
            && _unitTypeUi.value.priceYear.trim() == "0"
        ) {
            _isUpdateSuccess.value = ValidationResult(true, "Masukkan minimal 1 Harga")
            return
        }

        if (!_isNameValid.value.isError) {
            viewModelScope.launch {
                val unitType = UnitType(
                    id = _unitTypeUi.value.id,
                    name = _unitTypeUi.value.name,
                    note = _unitTypeUi.value.note,
                    priceGuarantee = cleanCurrencyFormatter(_unitTypeUi.value.priceGuarantee),
                    priceDay = cleanCurrencyFormatter(_unitTypeUi.value.priceDay),
                    priceWeek = cleanCurrencyFormatter(_unitTypeUi.value.priceWeek),
                    priceMonth = cleanCurrencyFormatter(_unitTypeUi.value.priceMonth),
                    priceThreeMonth = cleanCurrencyFormatter(_unitTypeUi.value.priceThreeMonth),
                    priceSixMonth = cleanCurrencyFormatter(_unitTypeUi.value.priceSixMonth),
                    priceYear = cleanCurrencyFormatter(_unitTypeUi.value.priceYear),
                    isDelete = false
                )
                updateUnitType(unitType)
            }
        }
    }

    private suspend fun updateUnitType(unitType: UnitType) {
        unitTypeRepository.updateUnitType(unitType)
        _isUpdateSuccess.value = ValidationResult(false)
    }
}

class UpdateUnitTypeViewModelFactory(
    private val unitTyRepository: UnitTypeRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateUnitTypeViewModel::class.java)) {
            return UpdateUnitTypeViewModel(unitTyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}