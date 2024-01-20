package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.Kost
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCashFlowViewModel(
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _stateUi: MutableStateFlow<AddCashFlowUi> =
        MutableStateFlow(
            AddCashFlowUi(
                tenantId = "0",
                tenantName = "Pilih Penyewa",
                kostId = "0",
                kostName = "Pilih Kost",
                unitId = "0",
                unitName = "Pilih Unit/Kamar"
            )
        )
    val stateUi: StateFlow<AddCashFlowUi>
        get() = _stateUi

    private val _stateListTenant: MutableStateFlow<UiState<List<Tenant>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListTenant: StateFlow<UiState<List<Tenant>>>
        get() = _stateListTenant

    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    private val _stateListUnit: MutableStateFlow<UiState<List<UnitAdapter>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListUnit: StateFlow<UiState<List<UnitAdapter>>>
        get() = _stateListUnit

    private val _isKostSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostSelectedValid: StateFlow<ValidationResult>
        get() = _isKostSelectedValid

    private val _isNoteValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNoteValid: StateFlow<ValidationResult>
        get() = _isNoteValid

    private val _isNominalValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNominalValid: StateFlow<ValidationResult>
        get() = _isNominalValid

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess


    init {
        _stateUi.value = stateUi.value.copy(createAt = generateDateNow())
    }

    fun getTenant() {
        clearError()

        viewModelScope.launch {
            _stateListTenant.value = UiState.Loading
            try {
                val data = tenantRepository.getAllTenantActive()
                _stateListTenant.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListTenant.value = UiState.Error(e.message.toString())
            }
        }

    }

    fun setTenantSelected(id: String, name: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(tenantId = id, tenantName = name)
    }

    fun getKost() {
        clearError()

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

    fun setKostSelected(id: String, name: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(kostId = id, kostName = name)
    }

    fun getUnit() {
        clearError()

        //check Kost Selected
        if (stateUi.value.kostId == "0") {
            _isKostSelectedValid.value = ValidationResult(true, "Pilih Kost Gan")
            _isProsesSuccess.value = ValidationResult(true, "Pilih Kost Gan")
            return
        }

        viewModelScope.launch {
            _stateListUnit.value = UiState.Loading
            try {
                val data = unitRepository.getUnitByKost(
                    kostId = stateUi.value.kostId
                )
                _stateListUnit.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnit.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setUnitSelected(id: String, name: String, unitTypeName: String) {
        clearError()
        _stateUi.value =
            stateUi.value.copy(
                unitId = id,
                unitName = name,
                unitTypeName = unitTypeName
            )
    }

    fun setNominal(value: String) {
        clearError()
        val valueFormat = currencyFormatterString(value)
        _stateUi.value = stateUi.value.copy(nominal = valueFormat)

        if (cleanCurrencyFormatter(stateUi.value.nominal) < 1) {
            _isNominalValid.value = ValidationResult(true, "Masukkan Nominal")
        } else {
            _isNominalValid.value = ValidationResult(false, "")
        }

        if (cleanCurrencyFormatter(stateUi.value.nominal) > 0 && stateUi.value.note.isEmpty()) {
            _isNoteValid.value =
                ValidationResult(true, "Masukkan Keterangan")
            return
        } else {
            _isNoteValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)

        if (cleanCurrencyFormatter(stateUi.value.nominal) > 0 && stateUi.value.note.isEmpty()) {
            _isNoteValid.value =
                ValidationResult(true, "Masukkan Keterangan")
            return
        } else {
            _isNoteValid.value = ValidationResult(false, "")
        }
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCash = value)
    }

    fun setCashFlowType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCashOut = value)
    }

    fun setPaymentDate(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(createAt = dateDialogToUniversalFormat(value))
    }

    fun dataIsComplete(): Boolean {
        clearError()
        //Check Nominal
        if (cleanCurrencyFormatter(stateUi.value.nominal) < 1) {
            _isNominalValid.value = ValidationResult(true, "Masukkan Nominal")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Nominal")
            return false
        }
        //check note
        if (stateUi.value.note.trim().isEmpty()) {
            _isNoteValid.value = ValidationResult(true, "Masukkan Keterangan")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Keterangan")
            return false
        }
        return true
    }

    fun process() {
        clearError()
        try {
            viewModelScope.launch {
                var cashFlow = CashFlow(
                    id = UUID.randomUUID().toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(stateUi.value.nominal).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = if (stateUi.value.isCashOut) 1 else 0,
                    creditTenantId = "0",
                    creditDebitId = "0",
                    unitId = stateUi.value.unitId,
                    tenantId = stateUi.value.tenantId,
                    kostId = stateUi.value.kostId,
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )
                var note = ""
                if (stateUi.value.isCashOut){
                    if (stateUi.value.tenantId != "0") {
                        note += "Untuk ${stateUi.value.tenantName}"
                    }
                    if (stateUi.value.kostId != "0") {
                        if (note.trim().isEmpty()) {
                            note += "Untuk ${stateUi.value.kostName}"
                        }else{
                            note += " Pada ${stateUi.value.kostName}"
                        }
                    }
                    if (stateUi.value.unitId != "0") {
                        if (note.trim().isEmpty()) {
                            note += "Untuk Unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName}"
                        }else{
                            note += " Unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName}"
                        }
                    }
                    note = "Pengeluaran ${stateUi.value.note}, $note"
                } else {
                    if (stateUi.value.tenantId != "0") {
                        note += "Dari ${stateUi.value.tenantName}"
                    }
                    if (stateUi.value.kostId != "0") {
                        if (note.trim().isEmpty()) {
                            note += "Untuk ${stateUi.value.kostName}"
                        }else{
                            note += " Pada ${stateUi.value.kostName}"
                        }
                    }
                    if (stateUi.value.unitId != "0") {
                        if (note.trim().isEmpty()) {
                            note += "Untuk Unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName}"
                        }else{
                            note += " Unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName}"
                        }
                    }
                    note = "Pemasukan ${stateUi.value.note}, $note"
                }

                cashFlow = cashFlow.copy(note = note)

                cashFlowRepository.insert(cashFlow)
                _isProsesSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    private fun clearError() {
        _isKostSelectedValid.value = ValidationResult(true, "")
        _stateListTenant.value = UiState.Error("")
        _stateListUnit.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isProsesSuccess.value = ValidationResult(true, "")
    }


}

class AddCashFlowViewModelFactory(
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCashFlowViewModel::class.java)) {
            return AddCashFlowViewModel(
                tenantRepository,
                unitRepository,
                kostRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}