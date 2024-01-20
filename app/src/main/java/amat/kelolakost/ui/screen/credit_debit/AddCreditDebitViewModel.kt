package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditDebit
import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CreditDebitRepository
import amat.kelolakost.data.repository.CustomerCreditDebitRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCreditDebitViewModel(
    private val repository: CreditDebitRepository,
    private val repositoryCustomer: CustomerCreditDebitRepository
) : ViewModel() {
    private val _stateUi: MutableStateFlow<AddCreditDebitUi> =
        MutableStateFlow(
            AddCreditDebitUi(
                customerCreditDebitId = "0",
                customerCreditDebitName = "Pilih Pelanggan"
            )
        )
    val stateUi: StateFlow<AddCreditDebitUi>
        get() = _stateUi

    private val _isCustomerSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isCustomerSelectedValid: StateFlow<ValidationResult>
        get() = _isCustomerSelectedValid

    private val _stateListCustomer: MutableStateFlow<UiState<List<CustomerCreditDebit>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListCustomer: StateFlow<UiState<List<CustomerCreditDebit>>>
        get() = _stateListCustomer

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
        _stateUi.value = stateUi.value.copy(
            createAt = generateDateNow(),
            dueDate = addDateLimitApp(generateDateNow(), "Bulan", 1)
        )
    }

    fun getCustomer() {
        clearError()

        viewModelScope.launch {
            _stateListCustomer.value = UiState.Loading
            try {
                val data = repositoryCustomer.getAllCustomerCreditDebit()
                _stateListCustomer.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListCustomer.value = UiState.Error(e.message.toString())
            }
        }

    }

    fun setCustomerSelected(customerCreditDebitId: String, customerCreditDebitName: String) {
        clearError()
        _isCustomerSelectedValid.value = ValidationResult(true)
        _stateUi.value = stateUi.value.copy(
            customerCreditDebitId = customerCreditDebitId,
            customerCreditDebitName = customerCreditDebitName
        )
    }

    fun setType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCredit = value)
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCash = value)
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
        if (stateUi.value.note.trim().isEmpty()) {
            _isNoteValid.value = ValidationResult(true, "Masukkan Keterangan")
        } else {
            _isNoteValid.value = ValidationResult(false, "")
        }

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

    fun setPaymentDate(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(createAt = dateDialogToUniversalFormat(value))
    }

    fun setDueDate(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(dueDate = dateDialogToUniversalFormat(value))
    }

    fun dataIsComplete(): Boolean {
        clearError()

        //check Tenant Selected
        if (stateUi.value.customerCreditDebitId == "0") {
            _isCustomerSelectedValid.value = ValidationResult(true, "Pilih Akun Pelanggan Gan")
            _isProsesSuccess.value = ValidationResult(true, "Pilih Akun Pelanggan Gan")
            return false
        }

        if (cleanCurrencyFormatter(stateUi.value.nominal) < 1) {
            _isNominalValid.value = ValidationResult(true, "Masukkan Nominal")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Nominal")
            return false
        }

        if (stateUi.value.note.trim().isEmpty()) {
            _isNoteValid.value = ValidationResult(true, "Masukkan Keterangan")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Keterangan")
            return false
        }

        return true
    }

    fun proses() {
        try {
            viewModelScope.launch {

                val cashFlowid = UUID.randomUUID().toString()
                val creditDebitId = UUID.randomUUID().toString()

                val noteCreditDebit: String = if (stateUi.value.isCredit){
                    "Hutang dari ${stateUi.value.customerCreditDebitName} Tanggal Jatuh tempo ${dateToDisplayMidFormat(stateUi.value.dueDate)}\n${stateUi.value.note}"
                }else{
                    "Piutang oleh ${stateUi.value.customerCreditDebitName} Tanggal Jatuh tempo ${dateToDisplayMidFormat(stateUi.value.dueDate)}\n${stateUi.value.note}"
                }

                val creditDebit = CreditDebit(
                    id = creditDebitId,
                    note = noteCreditDebit,
                    status = if (stateUi.value.isCredit) 0 else 1,
                    remaining = cleanCurrencyFormatter(stateUi.value.nominal),
                    customerCreditDebitId = stateUi.value.customerCreditDebitId,
                    dueDate = stateUi.value.dueDate,
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                val noteCashFlow : String = if (stateUi.value.isCredit){
                    "Hutang dari ${stateUi.value.customerCreditDebitName} Tanggal Jatuh tempo ${dateToDisplayMidFormat(stateUi.value.dueDate)}\n${stateUi.value.note}"
                }else{
                    "Piutang oleh ${stateUi.value.customerCreditDebitName} Tanggal Jatuh tempo ${dateToDisplayMidFormat(stateUi.value.dueDate)}\n${stateUi.value.note}"
                }

                val cashFlow = CashFlow(
                    id = cashFlowid,
                    note = noteCashFlow,
                    nominal = cleanCurrencyFormatter(stateUi.value.nominal).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = if (stateUi.value.isCredit) 0 else 1,
                    creditDebitId = creditDebitId,
                    creditTenantId = "0",
                    unitId = "0",
                    tenantId = "0",
                    kostId = "0",
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                repository.insertCreditDebit(creditDebit = creditDebit, cashFlow = cashFlow)

                _isProsesSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }
    }

    fun clearError() {
        _stateListCustomer.value = UiState.Error("")
        _isProsesSuccess.value = ValidationResult(true, "")
    }


}

class AddCreditDebitViewModelFactory(
    private val repository: CreditDebitRepository,
    private val repositoryCustomer: CustomerCreditDebitRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCreditDebitViewModel::class.java)) {
            return AddCreditDebitViewModel(repository, repositoryCustomer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}