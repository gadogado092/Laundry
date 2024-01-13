package amat.kelolakost.ui.screen.move

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.data.UnitHome
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.generateDateNow
import amat.kelolakost.getLimitDay
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.UUID

class MoveViewModel(
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _stateUnit: MutableStateFlow<UiState<UnitHome>> =
        MutableStateFlow(UiState.Loading)

    private val _moveUi: MutableStateFlow<MoveUi> =
        MutableStateFlow(MoveUi(unitIdMove = "0", unitNameMove = "Pilih Unit/Kamar"))
    val moveUi: StateFlow<MoveUi>
        get() = _moveUi

    private val _stateListUnit: MutableStateFlow<UiState<List<UnitAdapter>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListUnit: StateFlow<UiState<List<UnitAdapter>>>
        get() = _stateListUnit

    private val _isMoveSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isMoveSuccess: StateFlow<ValidationResult>
        get() = _isMoveSuccess

    private val _isUnitMoveSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUnitMoveSelectedValid: StateFlow<ValidationResult>
        get() = _isUnitMoveSelectedValid

    private val _isNoteMaintenanceValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNoteMaintenanceValid: StateFlow<ValidationResult>
        get() = _isNoteMaintenanceValid

    private val _isNominalValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNominalValid: StateFlow<ValidationResult>
        get() = _isNominalValid

    private val _isDownPaymentValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDownPaymentValid: StateFlow<ValidationResult>
        get() = _isDownPaymentValid

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            userRepository.getDetail()
                .catch {

                }
                .collect { data ->
                    _user.value = data
                }
        }
    }

    fun getDetail(unitId: String) {
        _stateUnit.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = unitRepository.getDetailUnit(unitId)
                _stateUnit.value = UiState.Success(data)

                val totalDebt = creditTenantRepository.getTotalDebt(data.tenantId)

                _moveUi.value = moveUi.value.copy(
                    unitId = data.id,
                    unitName = data.name,
                    unitTypeName = data.unitTypeName,
                    tenantId = data.tenantId,
                    tenantName = data.tenantName,
                    kostId = data.kostId,
                    kostName = data.kostName,
                    limitCheckOut = data.limitCheckOut,
                    currentDebtTenant = totalDebt,
                    moveDate = generateDateNow()
                )

            } catch (e: Exception) {
                _stateUnit.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun getUnit() {
        clearError()
        viewModelScope.launch {
            _stateListUnit.value = UiState.Loading
            try {
                val data = unitRepository.getUnitByKost(
                    kostId = moveUi.value.kostId,
                    unitStatusId = "2"
                )
                _stateListUnit.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnit.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setStatusAfterCheckOut(value: String) {
        _moveUi.value = moveUi.value.copy(statusAfterCheckOut = value)
        setNoteMaintenance("")
    }

    fun setNoteMaintenance(value: String) {
        clearError()
        _moveUi.value = moveUi.value.copy(noteMaintenance = value)
        if (moveUi.value.statusAfterCheckOut != "Siap Digunakan" && moveUi.value.noteMaintenance.isEmpty()) {
            _isNoteMaintenanceValid.value =
                ValidationResult(true, "Masukkan Catatan")
        } else {
            _isNoteMaintenanceValid.value =
                ValidationResult(false)
        }
    }

    fun setMoveType(value: String) {
        _moveUi.value = moveUi.value.copy(moveType = value)
        setNominal("")
    }

    fun setNominal(value: String) {
        clearError()
        val valueFormat = currencyFormatterString(value)
        _moveUi.value = moveUi.value.copy(nominal = valueFormat)
        if (moveUi.value.nominal != "0" && moveUi.value.nominal.isEmpty()) {
            _isNominalValid.value =
                ValidationResult(true, "Masukkan Nominal")
        } else {
            _isNominalValid.value =
                ValidationResult(false)
        }

        refreshDataUi()
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _moveUi.value = moveUi.value.copy(isCash = value)
    }

    fun setPaymentMethod(value: Boolean) {
        clearError()
        _moveUi.value = moveUi.value.copy(isFullPayment = value)
        setDownPayment("0")
    }

    fun setDownPayment(value: String) {
        clearError()
        val valueFormat = currencyFormatterString(value)
        _moveUi.value = moveUi.value.copy(downPayment = valueFormat)
        _isDownPaymentValid.value = ValidationResult(true, "")
        if (moveUi.value.downPayment.trim()
                .isEmpty() || moveUi.value.downPayment.trim() == "0"
        ) {
            _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
        } else {
            _isDownPaymentValid.value = ValidationResult(false, "")
        }

        refreshDataUi()
    }

    private fun refreshDataUi() {
        if (moveUi.value.moveType == "Upgrade") {
            val total: BigInteger = cleanCurrencyFormatter(moveUi.value.nominal).toBigInteger()
            if (moveUi.value.isFullPayment) {
                _moveUi.value = moveUi.value.copy(totalPayment = total.toString())
            } else {
                val downPayment = cleanCurrencyFormatter(moveUi.value.downPayment)
                val currentDebtTenant =
                    cleanCurrencyFormatter(moveUi.value.currentDebtTenant.toString())
                val debtTenant: BigInteger = total - downPayment.toBigInteger()
                val totalDebtTenant: BigInteger = debtTenant + currentDebtTenant.toBigInteger()

                _moveUi.value = moveUi.value.copy(
                    totalPayment = downPayment.toString(),
                    debtTenantMove = debtTenant.toString(),
                    totalDebtTenant = totalDebtTenant.toString()
                )
            }
        }
    }

    private fun clearError() {
        _stateListUnit.value = UiState.Error("")
        _isMoveSuccess.value = ValidationResult(true, "")
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (moveUi.value.statusAfterCheckOut != "Siap Digunakan" && moveUi.value.noteMaintenance.isEmpty()) {
            _isNoteMaintenanceValid.value =
                ValidationResult(true, "Masukkan Catatan")
            _isMoveSuccess.value =
                ValidationResult(true, "Masukkan Catatan")
            return false
        }

        //check Unit Selected
        if (moveUi.value.unitIdMove == "0") {
            _isUnitMoveSelectedValid.value = ValidationResult(true, "Pilih Unit/Kamar Tujuan Bro")
            _isMoveSuccess.value = ValidationResult(true, "Pilih Unit/Kamar Tujuan Bro")
            return false
        }

        if (moveUi.value.moveType != "Gratis") {
            if (moveUi.value.nominal != "0" && moveUi.value.nominal.isEmpty()) {
                _isNominalValid.value =
                    ValidationResult(true, "Masukkan Nominal")
                _isMoveSuccess.value = ValidationResult(true, "Masukkan Nominal")
                return false
            } else {
                _isNominalValid.value =
                    ValidationResult(false)
            }
        }

        if (moveUi.value.moveType == "Upgrade") {
            //check bayar cicil
            if (!moveUi.value.isFullPayment) {
                if (moveUi.value.downPayment.trim()
                        .isEmpty() || moveUi.value.downPayment.trim() == "0"
                ) {
                    _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
                    _isMoveSuccess.value = ValidationResult(true, "Masukkan Uang Muka")
                    return false
                }

                if (moveUi.value.debtTenantMove.toBigInteger() < 1.toBigInteger()) {
                    _isMoveSuccess.value =
                        ValidationResult(true, "Pilih Metode Pembayaran LUNAS")
                    return false
                }
            }
        }

        return true
    }

    fun prosesCheckOut() {
        clearError()
        viewModelScope.launch {
            try {
                viewModelScope.launch {
                    val cashFlowid = UUID.randomUUID()
                    var statusIdUnitOld = 1

                    when (moveUi.value.statusAfterCheckOut) {
                        "Siap Digunakan" -> statusIdUnitOld = 2
                        "Pembersihan" -> statusIdUnitOld = 3
                        "Perbaikan" -> statusIdUnitOld = 4
                    }

                    var creditTenant = CreditTenant(
                        id = "0",
                        note = "",
                        tenantId = moveUi.value.tenantId,
                        status = 0,
                        remainingDebt = cleanCurrencyFormatter(moveUi.value.debtTenantMove),
                        kostId = moveUi.value.kostId,
                        unitId = moveUi.value.unitIdMove,
                        createAt = moveUi.value.moveDate,
                        isDelete = false
                    )

                    var cashFlow = CashFlow(
                        id = cashFlowid.toString(),
                        note = "",
                        nominal = cleanCurrencyFormatter(moveUi.value.totalPayment).toString(),
                        typePayment = if (moveUi.value.isCash) 1 else 0,
                        type = 0,
                        creditTenantId = "0",
                        creditId = "0",
                        debitId = "0",
                        unitId = moveUi.value.unitIdMove,
                        tenantId = moveUi.value.tenantId,
                        kostId = moveUi.value.kostId,
                        createAt = moveUi.value.moveDate,
                        isDelete = false
                    )

                    if (moveUi.value.moveType == "Downgrade") {
                        val note =
                            "Pengembalian dana ke ${moveUi.value.tenantName}, Pindah Unit dari ${moveUi.value.unitName} ke Unit ${moveUi.value.unitNameMove}"

                        cashFlow =
                            cashFlow.copy(
                                note = note,
                                unitId = moveUi.value.unitId,
                                type = 1,
                                nominal = cleanCurrencyFormatter(moveUi.value.nominal).toString()
                            )

                    } else if (moveUi.value.moveType == "Upgrade") {

                        if (moveUi.value.isFullPayment) {
                            val note =
                                "Pembayaran Lunas untuk Pindah Unit dari ${moveUi.value.unitName} ke Unit ${moveUi.value.unitNameMove} oleh ${moveUi.value.tenantName}"

                            cashFlow = cashFlow.copy(
                                note = note,
                                unitId = moveUi.value.unitIdMove,
                                type = 0
                            )

                        } else {
                            val creditTenantId = UUID.randomUUID().toString()
                            var note =
                                "Pembayaran Uang Muka/Dp oleh ${moveUi.value.tenantName} untuk Pindah Unit dari ${moveUi.value.unitName} ke Unit ${moveUi.value.unitNameMove}"
                            note += " \nSisa tagihan ${currencyFormatterStringViewZero(moveUi.value.debtTenantMove)}"

                            cashFlow = cashFlow.copy(note = note, creditTenantId = creditTenantId)

                            //NOTE HUTANG
                            var noteDebt =
                                "Pembayaran Uang Muka/Dp oleh ${moveUi.value.tenantName} untuk Pindah Unit dari ${moveUi.value.unitName} ke Unit ${moveUi.value.unitNameMove}"
                            noteDebt += " \nSisa tagihan ${currencyFormatterStringViewZero(moveUi.value.debtTenantMove)}"
                            creditTenant = creditTenant.copy(note = noteDebt, id = creditTenantId)

                        }
                    }

                    cashFlowRepository.prosesMoveUnit(
                        cashFlow = cashFlow,
                        creditTenant = creditTenant,
                        unitIdOld = moveUi.value.unitId,
                        statusIdUnitOld = statusIdUnitOld,
                        noteMaintenanceUnitOld = moveUi.value.noteMaintenance,
                        moveType = moveUi.value.moveType,
                        isFullPayment = moveUi.value.isFullPayment
                    )

                    _isMoveSuccess.value = ValidationResult(false)
                }
            } catch (e: Exception) {
                _isMoveSuccess.value = ValidationResult(true, e.message.toString())
            }
        }
    }


    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(user.value.limit)
            day.toInt() < 0
        } catch (e: Exception) {
            false
        }
    }

    fun setUnitSelected(id: String, name: String, unitTypeName: String) {
        clearError()
        _isUnitMoveSelectedValid.value = ValidationResult(false)
        _moveUi.value =
            moveUi.value.copy(unitIdMove = id, unitNameMove = name, unitTypeNameMove = unitTypeName)
        _stateListUnit.value = UiState.Error("")
    }

}

class MoveViewModelFactory(
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoveViewModel::class.java)) {
            return MoveViewModel(
                unitRepository,
                creditTenantRepository,
                userRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}