package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.UnitTypeRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddUnitTypeViewModel(private val unitTypeRepository: UnitTypeRepository) : ViewModel() {
    private val _unitTypeUi: MutableStateFlow<UnitTypeUi> =
        MutableStateFlow(UnitTypeUi())
    val unitTypeUi: StateFlow<UnitTypeUi>
        get() = _unitTypeUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isInsertSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isInsertSuccess: StateFlow<ValidationResult>
        get() = _isInsertSuccess

    fun setName(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(name = value)
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setPriceGuarantee(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = valueFormat)
        }
    }

    fun setPriceDay(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = valueFormat)
        }
    }

    fun setPriceWeek(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = valueFormat)
        }
    }

    fun setPriceMonth(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = valueFormat)
        }
    }

    fun setPriceThreeMonth(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = valueFormat)
        }
    }

    fun setPriceSixMonth(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = valueFormat)
        }
    }

    fun setPriceYear(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
//        _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = valueFormat)
        }
    }

    fun setNote(value: String) {
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitTypeUi.value = _unitTypeUi.value.copy(note = value)
    }

    fun prosesInsert() {
        _isInsertSuccess.value = ValidationResult(true, "")
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isInsertSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }

        if ((_unitTypeUi.value.priceDay.trim()
                .isEmpty() || _unitTypeUi.value.priceDay.trim() == "0")
            && (_unitTypeUi.value.priceWeek.trim()
                .isEmpty() || _unitTypeUi.value.priceWeek.trim() == "0")
            && (_unitTypeUi.value.priceMonth.trim()
                .isEmpty() || _unitTypeUi.value.priceMonth.trim() == "0")
            && (_unitTypeUi.value.priceThreeMonth.trim()
                .isEmpty() || _unitTypeUi.value.priceThreeMonth.trim() == "0")
            && (_unitTypeUi.value.priceSixMonth.trim()
                .isEmpty() || _unitTypeUi.value.priceSixMonth.trim() == "0")
            && (_unitTypeUi.value.priceYear.trim()
                .isEmpty() || _unitTypeUi.value.priceYear.trim() == "0")
        ) {
            _isInsertSuccess.value = ValidationResult(true, "Masukkan minimal 1 Harga")
            return
        }

        if (!_isNameValid.value.isError) {
            viewModelScope.launch {
                val id = UUID.randomUUID()

                val unitType = UnitType(
                    id = id.toString(),
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
                insertUnitType(unitType)
            }
        }
    }

    private suspend fun insertUnitType(unitType: UnitType) {
        unitTypeRepository.insertUnitType(unitType)
        _isInsertSuccess.value = ValidationResult(false)
    }
}

class AddUnitTypeViewModelFactory(
    private val unitTyRepository: UnitTypeRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUnitTypeViewModel::class.java)) {
            return AddUnitTypeViewModel(unitTyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}