package amat.kelolakost.ui.screen.unit

import amat.kelolakost.data.Kost
import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddUnitViewModel(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val unitTypeRepository: UnitTypeRepository
) : ViewModel() {
    private val _unitUi: MutableStateFlow<UnitUi> =
        MutableStateFlow(
            UnitUi(
                kostId = "0",
                kostName = "Pilih Kost",
                unitTypeId = "0",
                unitTypeName = "Pilih Tipe Kamar/Unit"
            )
        )
    val unitUi: StateFlow<UnitUi>
        get() = _unitUi

    private val _isUnitNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUnitNameValid: StateFlow<ValidationResult>
        get() = _isUnitNameValid

    private val _isKostSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostSelectedValid: StateFlow<ValidationResult>
        get() = _isKostSelectedValid

    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    private val _isUnitTypeSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUnitTypeSelectedValid: StateFlow<ValidationResult>
        get() = _isUnitTypeSelectedValid

    private val _stateListUnitType: MutableStateFlow<UiState<List<UnitType>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListUnitType: StateFlow<UiState<List<UnitType>>>
        get() = _stateListUnitType

    private val _isInsertSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isInsertSuccess: StateFlow<ValidationResult>
        get() = _isInsertSuccess

    fun setUnitName(value: String) {
        _stateListUnitType.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(name = value)
        if (_unitUi.value.name.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
        } else {
            _isUnitNameValid.value = ValidationResult(false, "")
        }
    }

    fun setKostSelected(id: String, name: String) {
        _isKostSelectedValid.value = ValidationResult(true, "")
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(kostId = id, kostName = name)
        _stateListKost.value = UiState.Error("")
    }

    fun setUnitTypeSelected(id: String, name: String) {
        _isUnitTypeSelectedValid.value = ValidationResult(true, "")
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(unitTypeId = id, unitTypeName = name)
        _stateListUnitType.value = UiState.Error("")
    }

    fun setNote(value: String) {
        _stateListUnitType.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isInsertSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(note = value)
    }

    fun getKost() {
        _stateListKost.value = UiState.Error("")
        _stateListUnitType.value = UiState.Error("")
        _isInsertSuccess.value = ValidationResult(true, "")
        viewModelScope.launch {
            _stateListKost.value = UiState.Loading
            try {
                val data = kostRepository.getAllKostOrder()
                _stateListKost.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListKost.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getUnitType() {
        _stateListKost.value = UiState.Error("")
        _stateListUnitType.value = UiState.Error("")
        _isInsertSuccess.value = ValidationResult(true, "")
        viewModelScope.launch {
            _stateListUnitType.value = UiState.Loading
            try {
                val data = unitTypeRepository.getAllUnitTypeOrder()
                _stateListUnitType.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnitType.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun prosesInsert() {
        _stateListKost.value = UiState.Error("")
        _stateListUnitType.value = UiState.Error("")

        if (_unitUi.value.name.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
            _isInsertSuccess.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
            return
        }

        if (_unitUi.value.kostId == "0") {
            _isInsertSuccess.value = ValidationResult(true, "Silahkan Pilih Kost Bro")
            _isKostSelectedValid.value = ValidationResult(true, "Silahkan Pilih Kost Bro")
            return
        }
        if (_unitUi.value.unitTypeId == "0") {
            _isUnitTypeSelectedValid.value = ValidationResult(true, "Pilih Tipe Kamar/Unit Gan")
            _isInsertSuccess.value = ValidationResult(true, "Pilih Tipe Kamar/Unit Gan")
            return
        }

        if (!_isUnitNameValid.value.isError
            && _unitUi.value.kostId != "0"
            && _unitUi.value.unitTypeId != "0"
        ) {
            viewModelScope.launch {
                val id = UUID.randomUUID()

                val unit = Unit(
                    id = id.toString(),
                    name = unitUi.value.name,
                    note = unitUi.value.note,
                    noteMaintenance = "",
                    unitTypeId = unitUi.value.unitTypeId,
                    unitStatusId = 2,
                    tenantId = "0",
                    kostId = unitUi.value.kostId,
                    bookingId = "0",
                    isDelete = false
                )
                insertUnit(unit)
            }
        }
    }

    private suspend fun insertUnit(unit: Unit) {
        try {
            unitRepository.insertUnit(unit)
            _isInsertSuccess.value = ValidationResult(false, "Tambah Data Berhasil")
        } catch (e: Exception) {
            _isInsertSuccess.value =
                ValidationResult(true, "Tambah Data Gagal " + e.message.toString())
        }

    }
}

class AddUnitViewModelFactory(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val unitTypeRepository: UnitTypeRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUnitViewModel::class.java)) {
            return AddUnitViewModel(unitRepository, kostRepository, unitTypeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}