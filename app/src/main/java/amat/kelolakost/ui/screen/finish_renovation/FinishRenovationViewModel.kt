package amat.kelolakost.ui.screen.finish_renovation

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.UnitHome
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

class FinishRenovationViewModel(
    private val unitRepository: UnitRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _stateUnit: MutableStateFlow<UiState<UnitHome>> =
        MutableStateFlow(UiState.Loading)

    private val _finishRenovationUi: MutableStateFlow<FinishRenovationUi> =
        MutableStateFlow(FinishRenovationUi())
    val finishRenovationUi: StateFlow<FinishRenovationUi>
        get() = _finishRenovationUi

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

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

                _finishRenovationUi.value = finishRenovationUi.value.copy(
                    unitId = data.id,
                    unitName = data.name,
                    unitTypeName = data.unitTypeName,
                    kostId = data.kostId,
                    noteMaintenance = data.noteMaintenance,
                    kostName = data.kostName,
                    unitStatusId = data.unitStatusId,
                    finishDate = generateDateNow()
                )
                _stateUnit.value = UiState.Success(data)

            } catch (e: Exception) {
                _stateUnit.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun setNoteMaintenance(value: String) {
        clearError()
        _finishRenovationUi.value = finishRenovationUi.value.copy(noteMaintenance = value)
    }

    fun setCostMaintenance(value: String) {
        clearError()
        val valueFormat = currencyFormatterString(value)
        _finishRenovationUi.value = finishRenovationUi.value.copy(costMaintenance = valueFormat)
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _finishRenovationUi.value = finishRenovationUi.value.copy(isCash = value)
    }

    fun dataIsComplete(): Boolean {
        clearError()

        return true
    }

    fun proses() {
        clearError()
        try {
            viewModelScope.launch {
                val cashFlowid = UUID.randomUUID()


                var cashFlow = CashFlow(
                    id = cashFlowid.toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(finishRenovationUi.value.costMaintenance).toString(),
                    typePayment = if (finishRenovationUi.value.isCash) 1 else 0,
                    type = 1,
                    creditTenantId = "0",
                    creditDebitId = "0",
                    unitId = finishRenovationUi.value.unitId,
                    tenantId = "0",
                    kostId = finishRenovationUi.value.kostId,
                    createAt = finishRenovationUi.value.finishDate,
                    isDelete = false
                )

                if (finishRenovationUi.value.unitStatusId == 3) {
                    val note =
                        "Pembersihan kost ${finishRenovationUi.value.kostName} pada unit ${finishRenovationUi.value.unitName}-${finishRenovationUi.value.unitTypeName} ${finishRenovationUi.value.noteMaintenance}"
                    cashFlow = cashFlow.copy(note = note)
                } else if (finishRenovationUi.value.unitStatusId == 4) {
                    val note =
                        "Perbaikan kost ${finishRenovationUi.value.kostName} pada unit ${finishRenovationUi.value.unitName}-${finishRenovationUi.value.unitTypeName} ${finishRenovationUi.value.noteMaintenance}"
                    cashFlow = cashFlow.copy(note = note)
                }

                cashFlowRepository.prosesFinishMaintenance(cashFlow)
                _isProsesSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    private fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class FinishRenovationViewModelFactory(
    private val unitRepository: UnitRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishRenovationViewModel::class.java)) {
            return FinishRenovationViewModel(
                unitRepository,
                userRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}