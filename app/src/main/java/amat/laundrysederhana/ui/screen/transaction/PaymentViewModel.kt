package amat.laundrysederhana.ui.screen.transaction

import amat.laundrysederhana.data.DetailTransaction
import amat.laundrysederhana.data.ProductCart
import amat.laundrysederhana.data.TransactionLaundry
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.CartRepository
import amat.laundrysederhana.data.repository.TransactionRepository
import amat.laundrysederhana.dateTimeToKodeInvoice
import amat.laundrysederhana.generateDateTimeNow
import amat.laundrysederhana.generateZeroInvoice
import amat.laundrysederhana.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _stateProduct: MutableStateFlow<UiState<List<ProductCart>>> =
        MutableStateFlow(UiState.Loading)
    val stateProduct: StateFlow<UiState<List<ProductCart>>>
        get() = _stateProduct

    private val _stateUi: MutableStateFlow<PaymentUi> =
        MutableStateFlow(PaymentUi())
    val stateUi: StateFlow<PaymentUi>
        get() = _stateUi

    private val _transactionId: MutableStateFlow<String> =
        MutableStateFlow("")
    val transactionId: StateFlow<String>
        get() = _transactionId

    private val _isCustomerNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isCustomerNameValid: StateFlow<ValidationResult>
        get() = _isCustomerNameValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed


    fun getProduct() {
        viewModelScope.launch {
            _stateProduct.value = UiState.Loading
            try {
                val totalPrice = cartRepository.getTotalPriceCart()
                if (totalPrice.total == null) {
                    totalPrice.total = "0"
                }
                _stateUi.value =
                    stateUi.value.copy(totalPrice = totalPrice.total!!)

                val cart = cartRepository.getCartList()
                _stateProduct.value = UiState.Success(cart)
            } catch (e: Exception) {
                Log.e("payment", e.message.toString())
                _stateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isFullPayment = value)
    }

    fun setCustomerName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(customerName = value)
        if (stateUi.value.customerName.trim().isEmpty()) {
            _isCustomerNameValid.value = ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
        } else {
            _isCustomerNameValid.value = ValidationResult(false, "")
        }
    }

    fun process(listDataProduct: List<ProductCart>) {
        try {
            viewModelScope.launch {
                val createAt = generateDateTimeNow()
                val dateInvoice = dateTimeToKodeInvoice(createAt)
                var lastNumberInvoice = 0

                val data = transactionRepository.getLastNumberInvoice(dateInvoice).lastCode
                if (data != null) {
                    lastNumberInvoice = data.substring(data.length - 4, data.length).toInt()
                }

                val newNumberInvoice =
                    if (lastNumberInvoice == 0) 1 else lastNumberInvoice.plus(1)

                val newCodeInvoice = dateInvoice + generateZeroInvoice(newNumberInvoice.toString())

                val transactionId = UUID.randomUUID().toString()
                _transactionId.value = transactionId

                val listDetailTransaction = mutableListOf<DetailTransaction>()
                listDataProduct.forEach { item ->
                    val id = UUID.randomUUID().toString()
                    listDetailTransaction.add(
                        DetailTransaction(
                            id = id,
                            transactionId = transactionId,
                            productId = item.productId,
                            categoryId = item.categoryId,
                            productName = item.productName,
                            note = item.note,
                            unit = item.unit,
                            price = item.productPrice,
                            qty = item.qty,
                            createAt = createAt,
                            totalPrice = item.productTotalPrice,
                            isDelete = false
                        )
                    )
                }

                val transaction = TransactionLaundry(
                    id = transactionId,
                    invoiceCode = newCodeInvoice,
                    customerId = "0",
                    customerName = stateUi.value.customerName.trim(),
                    laundryStatusId = 3,
                    isFullPayment = stateUi.value.isFullPayment,
                    totalPrice = stateUi.value.totalPrice,
                    note = stateUi.value.note,
                    createAt = createAt,
                    isDelete = false
                )

                transactionRepository.insertNewTransaction(transaction, listDetailTransaction)
                _isProsesFailed.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            Log.e("bossku", e.message.toString())
            _isProsesFailed.value = ValidationResult(true, e.message.toString())
        }
    }

    private fun clearError() {
        _isProsesFailed.value = ValidationResult(true, "")
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (stateUi.value.customerName.trim().isEmpty()) {
            _isCustomerNameValid.value = ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
            return false
        }
        return true
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val transactionRepository: TransactionRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                cartRepository, transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}