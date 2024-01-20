package amat.kelolakost.ui.screen.check_out

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.data.CashFlow
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
import java.util.UUID

class CheckOutViewModel(
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

    private val _checkOutUi: MutableStateFlow<CheckOutUi> =
        MutableStateFlow(CheckOutUi())
    val checkOutUi: StateFlow<CheckOutUi>
        get() = _checkOutUi

    private val _isCheckOutSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isCheckOutSuccess: StateFlow<ValidationResult>
        get() = _isCheckOutSuccess

    private val _isNoteMainTenanceValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNoteMainTenanceValid: StateFlow<ValidationResult>
        get() = _isNoteMainTenanceValid

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

                _checkOutUi.value = _checkOutUi.value.copy(
                    unitId = data.id,
                    unitName = data.name,
                    unitTypeName = data.unitTypeName,
                    tenantId = data.tenantId,
                    tenantName = data.tenantName,
                    kostId = data.kostId,
                    kostName = data.kostName,
                    priceGuarantee = data.priceGuarantee,
                    limitCheckOut = data.limitCheckOut,
                    debtTenant = totalDebt
                )

            } catch (e: Exception) {
                _stateUnit.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun setStatusAfterCheckOut(value: String) {
        _checkOutUi.value = checkOutUi.value.copy(statusAfterCheckOut = value)
        setNoteMaintenance("")
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _checkOutUi.value = checkOutUi.value.copy(isCash = value)
    }

    fun setNoteMaintenance(value: String) {

        _checkOutUi.value = checkOutUi.value.copy(noteMaintenance = value)
        if (checkOutUi.value.statusAfterCheckOut != "Siap Digunakan" && checkOutUi.value.noteMaintenance.isEmpty()) {
            _isNoteMainTenanceValid.value =
                ValidationResult(true, "Masukkan Catatan")
        } else {
            _isNoteMainTenanceValid.value =
                ValidationResult(false)
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (checkOutUi.value.statusAfterCheckOut != "Siap Digunakan" && checkOutUi.value.noteMaintenance.isEmpty()) {
            _isNoteMainTenanceValid.value =
                ValidationResult(true, "Masukkan Catatan")
            _isCheckOutSuccess.value =
                ValidationResult(true, "Masukkan Catatan")
            return false
        }

        return true
    }

    fun prosesCheckOut() {
        try {
            viewModelScope.launch {
                val cashFlowid = UUID.randomUUID()
                var unitStatusId = 1
                val createAt = generateDateNow()

                when (checkOutUi.value.statusAfterCheckOut) {
                    "Siap Digunakan" -> unitStatusId = 2
                    "Pembersihan" -> unitStatusId = 3
                    "Perbaikan" -> unitStatusId = 4
                }

                var cashFlow = CashFlow(
                    id = cashFlowid.toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(checkOutUi.value.priceGuarantee.toString()).toString(),
                    typePayment = if (checkOutUi.value.isCash) 1 else 0,
                    type = 1,
                    creditTenantId = "0",
                    creditDebitId = "0",
                    unitId = checkOutUi.value.unitId,
                    tenantId = checkOutUi.value.tenantId,
                    kostId = checkOutUi.value.kostId,
                    createAt = createAt,
                    isDelete = false
                )

                if (checkOutUi.value.priceGuarantee > 0) {
                    cashFlow = cashFlow.copy(note = "Pengembalian uang jaminan ke ${checkOutUi.value.tenantName} pada unit ${checkOutUi.value.unitName}-${checkOutUi.value.kostName}")
                }


                cashFlowRepository.prosesCheckOut(
                    cashFlow = cashFlow,
                    priceGuarantee = checkOutUi.value.priceGuarantee,
                    unitStatusId = unitStatusId,
                    noteMaintenance = checkOutUi.value.noteMaintenance
                )
            }
            _isCheckOutSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isCheckOutSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    fun clearError() {
        _isCheckOutSuccess.value = ValidationResult(true, "")
    }

    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(user.value.limit)
            day.toInt() < 0
        } catch (e: Exception) {
            false
        }
    }

}

class CheckOutViewModelFactory(
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckOutViewModel::class.java)) {
            return CheckOutViewModel(
                unitRepository,
                creditTenantRepository,
                userRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}