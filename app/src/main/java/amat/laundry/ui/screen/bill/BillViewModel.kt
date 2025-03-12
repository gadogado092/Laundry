package amat.laundry.ui.screen.bill

import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.DetailTransactionRepository
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillViewModel(
    private val repository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val detailTransactionRepository: DetailTransactionRepository
) : ViewModel() {

    private val _stateUi: MutableStateFlow<UiState<BillUi>> =
        MutableStateFlow(UiState.Loading)
    val stateUi: StateFlow<UiState<BillUi>>
        get() = _stateUi

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    private val _isProsesUpdateStatusFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesUpdateStatusFailed: StateFlow<ValidationResult>
        get() = _isProsesUpdateStatusFailed

    private val _dataInvoice: MutableStateFlow<BillUi> =
        MutableStateFlow(BillUi())

    val dataInvoice: StateFlow<BillUi>
        get() = _dataInvoice

    fun getData(transactionId: String) {
        clearError()
        if (transactionId != "") {

            _stateUi.value = UiState.Loading

            viewModelScope.launch {
                try {
                    val dataProfile = repository.getProfile()
                    val dataTransaction = transactionRepository.getTransaction(transactionId)
                    val listDetailTransaction =
                        detailTransactionRepository.getDetailTransactionList(transactionId)

                    val dataUi = BillUi(
                        businessName = dataProfile.businessName,
                        businessAddress = dataProfile.address,
                        businessNumberPhone = dataProfile.numberPhone,
                        footerNote = dataProfile.footerNote,
                        isFullPayment = dataTransaction.isFullPayment,

                        printerName = dataProfile.printerName,
                        printerAddress = dataProfile.printerAddress,

                        customerName = dataTransaction.customerName,
                        invoiceCode = dataTransaction.invoiceCode,
                        dateTimeTransaction = dataTransaction.createAt,
                        totalPrice = dataTransaction.totalPrice,
                        noteTransaction = dataTransaction.note,
                        listDetailTransaction = listDetailTransaction
                    )

                    _dataInvoice.value = dataUi
                    _stateUi.value = UiState.Success(dataUi)
                } catch (e: Exception) {
                    Log.e("bill", e.message.toString())
                    _stateUi.value = UiState.Error(e.message.toString())
                }

            }

        } else {
            _stateUi.value = UiState.Error("Id Transaksi Tidak Ada")
        }

    }

    fun deleteTransaction(transactionId: String) {
        clearError()
        if (transactionId != "") {

            viewModelScope.launch {
                try {
                    transactionRepository.deleteTransactionAndDetailTransaction(transactionId)
                    _isProsesDeleteFailed.value = ValidationResult(false)
                } catch (e: Exception) {
                    _isProsesDeleteFailed.value = ValidationResult(true, e.message.toString())
                }
            }

        } else {
            _isProsesDeleteFailed.value = ValidationResult(true, "Id Transaksi Tidak Ada")
        }
    }

    fun updateStatusTransaction(transactionId: String) {
        clearError()
        if (transactionId != "") {

            viewModelScope.launch {
                try {
                    transactionRepository.updateTransactionStatusPayment(transactionId, true)
                    _isProsesUpdateStatusFailed.value = ValidationResult(false)
                } catch (e: Exception) {
                    _isProsesUpdateStatusFailed.value = ValidationResult(true, e.message.toString())
                }
            }

        } else {
            _isProsesUpdateStatusFailed.value = ValidationResult(true, "Id Transaksi Tidak Ada")
        }
    }

    private fun clearError() {
        _isProsesDeleteFailed.value = ValidationResult(true, "")
        _isProsesUpdateStatusFailed.value = ValidationResult(true, "")
    }

}

class BillViewModelFactory(
    private val repository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val detailTransactionRepository: DetailTransactionRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            return BillViewModel(
                repository,
                transactionRepository,
                detailTransactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}