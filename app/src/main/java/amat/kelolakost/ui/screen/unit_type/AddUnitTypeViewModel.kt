package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.generateDateTimeNow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddUnitTypeViewModel(private val unitTyRepository: UnitTypeRepository) : ViewModel() {
    private val _unitTypeUi: MutableStateFlow<UnitTypeUi> =
        MutableStateFlow(UnitTypeUi())
    val unitTypeUi: StateFlow<UnitTypeUi>
        get() = _unitTypeUi

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isInsertSuccess: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isInsertSuccess: StateFlow<Boolean>
        get() = _isInsertSuccess

    fun setName(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(name = value)
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setPriceGuarantee(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceGuarantee = valueFormat)
        }
    }

    fun setPriceDay(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceDay = valueFormat)
        }
    }

    fun setPriceWeek(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceWeek = valueFormat)
        }
    }

    fun setPriceMonth(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceMonth = valueFormat)
        }
    }

    fun setPriceThreeMonth(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceThreeMonth = valueFormat)
        }
    }

    fun setPriceSixMonth(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceSixMonth = valueFormat)
        }
    }

    fun setPriceYear(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = value)
        if (value.trim().isEmpty() || value.trim() == "0") {
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = "")
        } else {
            val valueFormat = currencyFormatterString(value)
            _unitTypeUi.value = _unitTypeUi.value.copy(priceYear = valueFormat)
        }
    }

    fun setNote(value: String) {
        _unitTypeUi.value = _unitTypeUi.value.copy(note = value)
    }

    fun prosesInsert() {
        if (_unitTypeUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        }

        if (!_isNameValid.value.isError) {
            viewModelScope.launch {
                val id = UUID.randomUUID()
                val createAt = generateDateTimeNow()

//                val kost = _unitTypeUi.value.copy(id = kostId.toString(), createAt = createAt)
//                insertKost(kost)
            }
        }
    }

//    private suspend fun insertKost(kost: Kost) {
//        kostRepository.insertKost(kost)
//        _isInsertSuccess.value = true
//    }
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