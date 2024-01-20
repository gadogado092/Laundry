package amat.kelolakost.ui.screen.credit_tenant

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenantDetail
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.UUID

class PaymentCreditTenantViewModel(private val repository: CreditTenantRepository) : ViewModel() {
    private val _stateCreditTenant: MutableStateFlow<UiState<CreditTenantDetail>> =
        MutableStateFlow(UiState.Loading)
    val stateCreditTenant: StateFlow<UiState<CreditTenantDetail>>
        get() = _stateCreditTenant

    private val _stateUi: MutableStateFlow<PaymentCreditTenantUi> =
        MutableStateFlow(PaymentCreditTenantUi())
    val stateUi: StateFlow<PaymentCreditTenantUi>
        get() = _stateUi

    private val _isDownPaymentValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDownPaymentValid: StateFlow<ValidationResult>
        get() = _isDownPaymentValid

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    init {
        _stateUi.value = stateUi.value.copy(createAt = generateDateNow())
    }

    fun getDetailCreditTenant(creditTenantId: String) {
        _stateCreditTenant.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getDetailCreditTenant(creditTenantId)
                _stateUi.value = stateUi.value.copy(
                    tenantId = data.tenantId,
                    creditTenantId = data.creditTenantId,
                    remainingDebt = data.remainingDebt,
                    note = data.note,
                    unitId = data.unitId,
                    kostId = data.kostId
                )
                _stateCreditTenant.value = UiState.Success(data)
                refreshDataUI()
            }
        } catch (e: Exception) {
            _stateCreditTenant.value = UiState.Error(e.message.toString())
        }

    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCash = value)
    }

    fun setPaymentDate(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(createAt = dateDialogToUniversalFormat(value))
    }

    fun setPaymentMethod(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isFullPayment = value)
        setDownPayment("0")
    }

    fun setDownPayment(value: String) {
        clearError()
        _isDownPaymentValid.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _stateUi.value = stateUi.value.copy(downPayment = valueFormat)

        if (stateUi.value.downPayment.trim()
                .isEmpty() || stateUi.value.downPayment.trim() == "0"
        ) {
            _isDownPaymentValid.value = ValidationResult(true, "Masukkan Nominal")
        } else {
            _isDownPaymentValid.value = ValidationResult(false, "")
        }

        refreshDataUI()
    }

    private fun refreshDataUI() {
        val total: BigInteger = stateUi.value.remainingDebt.toBigInteger()

        if (stateUi.value.isFullPayment) {
            _stateUi.value = stateUi.value.copy(totalPayment = total.toString())
        } else {
            val downPayment = cleanCurrencyFormatter(stateUi.value.downPayment)
            val debtTenant: BigInteger = total - downPayment.toBigInteger()

            _stateUi.value = stateUi.value.copy(
                totalPayment = downPayment.toString(),
                debtTenant = debtTenant.toString()
            )
        }

    }

    fun dataIsComplete(): Boolean {
        clearError()
        //check bayar cicil
        if (!stateUi.value.isFullPayment) {
            if (stateUi.value.downPayment.trim()
                    .isEmpty() || stateUi.value.downPayment.trim() == "0"
            ) {
                _isDownPaymentValid.value = ValidationResult(true, "Masukkan Nominal")
                _isProsesSuccess.value = ValidationResult(true, "Masukkan Nominal")
                return false
            }

            if (stateUi.value.debtTenant.toBigInteger() < 1.toBigInteger()) {
                _isProsesSuccess.value =
                    ValidationResult(true, "Pilih Metode Pembayaran Pelunasan")
                return false
            }
        }
        return true
    }

    fun proses() {
        try {
            viewModelScope.launch {

                var cashFlow = CashFlow(
                    id = UUID.randomUUID().toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(stateUi.value.totalPayment).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = 0,
                    creditTenantId = _stateUi.value.creditTenantId,
                    creditDebitId = "0",
                    unitId = stateUi.value.unitId,
                    tenantId = stateUi.value.tenantId,
                    kostId = stateUi.value.kostId,
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                if (stateUi.value.isFullPayment) {
                    val note = "Pelunasan Hutang -> ${stateUi.value.note}"
                    cashFlow = cashFlow.copy(note = note)

                    repository.payDebt(
                        cashFlow = cashFlow,
                        remainingDebt = 0
                    )
                } else {
                    var note = "Angsuran Hutang -> ${stateUi.value.note}"
                    note +=
                        " \nSisa Tagihan ${currencyFormatterStringViewZero(stateUi.value.debtTenant)}"
                    cashFlow = cashFlow.copy(note = note)

                    repository.payDebt(
                        cashFlow = cashFlow,
                        remainingDebt = cleanCurrencyFormatter(stateUi.value.debtTenant)
                    )
                }

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

class PaymentCreditTenantViewModelFactory(private val repository: CreditTenantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentCreditTenantViewModel::class.java)) {
            return PaymentCreditTenantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}