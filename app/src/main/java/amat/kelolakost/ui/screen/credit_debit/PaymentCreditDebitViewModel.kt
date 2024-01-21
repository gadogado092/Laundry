package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditDebitHome
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CreditDebitRepository
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

class PaymentCreditDebitViewModel(
    private val creditDebitRepository: CreditDebitRepository
) : ViewModel() {
    private val _stateCreditDebit: MutableStateFlow<UiState<CreditDebitHome>> =
        MutableStateFlow(UiState.Loading)
    val stateCreditDebit: StateFlow<UiState<CreditDebitHome>>
        get() = _stateCreditDebit

    private val _stateUi: MutableStateFlow<PaymentCreditDebitUi> =
        MutableStateFlow(PaymentCreditDebitUi())
    val stateUi: StateFlow<PaymentCreditDebitUi>
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
        _stateUi.value = stateUi.value.copy(
            createAt = generateDateNow(),
            dueDate = addDateLimitApp(generateDateNow(), "Bulan", 1)
        )

    }

    fun getDetailCreditDebit(creditDebitId: String) {
        _stateCreditDebit.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = creditDebitRepository.getDetailCreditDebit(creditDebitId)
                _stateUi.value =
                    stateUi.value.copy(
                        creditDebitId = data.creditDebitId,
                        note = data.note,
                        remainingDebt = data.remaining,
                        status = data.status,
                        oldDueDate = data.dueDate,
                        creditDebitName = data.customerCreditDebitName
                    )
                _stateCreditDebit.value = UiState.Success(data)
                refreshDataUI()
            }
        } catch (e: Exception) {
            _stateCreditDebit.value = UiState.Error(e.message.toString())
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

    fun setDueDate(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(dueDate = dateDialogToUniversalFormat(value))
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
            val remaining: BigInteger = total - downPayment.toBigInteger()

            _stateUi.value = stateUi.value.copy(
                totalPayment = downPayment.toString(),
                remaining = remaining.toString()
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

            if (stateUi.value.remaining.toBigInteger() < 1.toBigInteger()) {
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
                val cashFlowid = UUID.randomUUID().toString()

                var cashFlow = CashFlow(
                    id = cashFlowid,
                    note = "",
                    nominal = cleanCurrencyFormatter(stateUi.value.totalPayment).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = if (stateUi.value.status == 0) 1 else 0,
                    creditDebitId = stateUi.value.creditDebitId,
                    creditTenantId = "0",
                    unitId = "0",
                    tenantId = "0",
                    kostId = "0",
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                val statusText = if (stateUi.value.status == 0) "Hutang" else "Piutang"

                if (stateUi.value.isFullPayment) {

                    val note = "Pelunasan $statusText -> ${stateUi.value.note}"
                    cashFlow = cashFlow.copy(note = note)

                    creditDebitRepository.payCreditDebit(
                        cashFlow = cashFlow,
                        remaining = 0,
                        dueDate = stateUi.value.oldDueDate,
                    )

                } else {

                    var note = "Angsuran $statusText -> ${stateUi.value.note}"
                    note +=
                        " \nSisa Tagihan ${currencyFormatterStringViewZero(stateUi.value.remaining)}"
                    cashFlow = cashFlow.copy(note = note)

                    creditDebitRepository.payCreditDebit(
                        cashFlow = cashFlow,
                        remaining = cleanCurrencyFormatter(stateUi.value.remaining),
                        dueDate = stateUi.value.dueDate
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

class PaymentCreditDebitViewModelFactory(
    private val creditDebitRepository: CreditDebitRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentCreditDebitViewModel::class.java)) {
            return PaymentCreditDebitViewModel(creditDebitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}

