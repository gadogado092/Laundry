package amat.kelolakost.ui.screen.unit

import amat.kelolakost.data.Kost
import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitDetail
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UpdateUnitViewModel(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val unitTypeRepository: UnitTypeRepository
) : ViewModel() {

    private val _stateUnitUi: MutableStateFlow<UiState<UnitDetail>> =
        MutableStateFlow(UiState.Loading)
    val stateUnitUi: StateFlow<UiState<UnitDetail>>
        get() = _stateUnitUi

    private val _unitUi: MutableStateFlow<UnitUi> =
        MutableStateFlow(
            UnitUi(
                kostId = "0",
                kostName = "Pilih Kost",
                unitTypeId = "0",
                unitTypeName = "Pilih Tipe Kamar/Lapangan"
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

    private val _isUpdateSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isUpdateSuccess: StateFlow<ValidationResult>
        get() = _isUpdateSuccess

    fun getDetail(id: String) {
        viewModelScope.launch {
            _stateUnitUi.value = UiState.Loading
            unitRepository.getDetail(id)
                .catch {
                    _stateUnitUi.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateUnitUi.value = UiState.Success(data)
                    _unitUi.value = UnitUi(
                        id = data.id,
                        name = data.name,
                        note = data.note,
                        kostId = data.kostId,
                        kostName = data.kostName,
                        unitTypeId = data.unitTypeId,
                        unitTypeName = data.unitTypeName,
                        noteMaintenance = data.noteMaintenance,
                        unitStatusId = data.unitStatusId,
                        tenantId = data.tenantId,
                        isDelete = data.isDelete
                    )
                }

        }
    }

    fun setUnitName(value: String) {
        _stateListUnitType.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(name = value)
        if (_unitUi.value.name.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
        } else {
            _isUnitNameValid.value = ValidationResult(false, "")
        }
    }

    fun setKostSelected(id: String, name: String) {
        _isKostSelectedValid.value = ValidationResult(true, "")
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(kostId = id, kostName = name)
        _stateListKost.value = UiState.Error("")
    }

    fun setUnitTypeSelected(id: String, name: String) {
        _isUnitTypeSelectedValid.value = ValidationResult(true, "")
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(unitTypeId = id, unitTypeName = name)
        _stateListUnitType.value = UiState.Error("")
    }

    fun setNote(value: String) {
        _stateListUnitType.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isUpdateSuccess.value = ValidationResult(true, "")
        _unitUi.value = _unitUi.value.copy(note = value)
    }

    fun getKost() {
        _stateListKost.value = UiState.Error("")
        _stateListUnitType.value = UiState.Error("")
        _isUpdateSuccess.value = ValidationResult(true, "")
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
        _isUpdateSuccess.value = ValidationResult(true, "")
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

    fun prosesUpdate() {
        _stateListKost.value = UiState.Error("")
        _stateListUnitType.value = UiState.Error("")

        if (_unitUi.value.name.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
            _isUpdateSuccess.value = ValidationResult(true, "Nama Unit Tidak Boleh Kosong")
            return
        }

        if (_unitUi.value.kostId == "0") {
            _isUpdateSuccess.value = ValidationResult(true, "Silahkan Pilih Kost Bro")
            _isKostSelectedValid.value = ValidationResult(true, "Silahkan Pilih Kost Bro")
            return
        }
        if (_unitUi.value.unitTypeId == "0") {
            _isUnitTypeSelectedValid.value = ValidationResult(true, "Pilih Tipe Kamar/Unit Gan")
            _isUpdateSuccess.value = ValidationResult(true, "Pilih Tipe Kamar/Unit Gan")
            return
        }

        if (!_isUnitNameValid.value.isError
            && _unitUi.value.kostId != "0"
            && _unitUi.value.unitTypeId != "0"
        ) {
            viewModelScope.launch {

                val unit = Unit(
                    id = unitUi.value.id,
                    name = unitUi.value.name,
                    note = unitUi.value.note,
                    noteMaintenance = unitUi.value.noteMaintenance,
                    unitTypeId = unitUi.value.unitTypeId,
                    unitStatusId = unitUi.value.unitStatusId,
                    tenantId = unitUi.value.tenantId,
                    kostId = unitUi.value.kostId,
                    isDelete = unitUi.value.isDelete
                )
                updateUnit(unit)
            }
        }
    }

    private suspend fun updateUnit(unit: Unit) {
        try {
            unitRepository.updateUnit(unit)
            _isUpdateSuccess.value = ValidationResult(false, "Update Data Berhasil")
        } catch (e: Exception) {
            _isUpdateSuccess.value =
                ValidationResult(true, "Update Data Gagal " + e.message.toString())
        }

    }
}

class UpdateUnitViewModelFactory(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val unitTypeRepository: UnitTypeRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateUnitViewModel::class.java)) {
            return UpdateUnitViewModel(unitRepository, kostRepository, unitTypeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}