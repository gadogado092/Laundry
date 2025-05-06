package amat.laundry.ui.screen.transaction

import amat.laundry.data.Cashier
import amat.laundry.data.Customer
import amat.laundry.data.DetailTransaction
import amat.laundry.data.ProductCart
import amat.laundry.data.TransactionLaundry
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CartRepository
import amat.laundry.data.repository.CashierRepository
import amat.laundry.data.repository.CustomerRepository
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.dateTimeToKodeInvoice
import amat.laundry.generateDateTimeNow
import amat.laundry.generateZeroInvoice
import amat.laundry.isNumberPhoneValid
import amat.laundry.ui.common.UiState
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
    private val transactionRepository: TransactionRepository,
    private val cashierRepository: CashierRepository,
    private val customerRepository: CustomerRepository
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

    private val _isOldCustomerValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isOldCustomerValid: StateFlow<ValidationResult>
        get() = _isOldCustomerValid

    private val _isNewCustomerNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNewCustomerNameValid: StateFlow<ValidationResult>
        get() = _isNewCustomerNameValid

    private val _isNewCustomerNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isNewCustomerNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isNewCustomerNumberPhoneValid

    private val _isCashierValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isCashierValid: StateFlow<ValidationResult>
        get() = _isCashierValid

    private val _stateListCashier: MutableStateFlow<UiState<List<Cashier>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListCashier: StateFlow<UiState<List<Cashier>>>
        get() = _stateListCashier

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    init {
        initCashierLastUsed()
    }

    private fun initCashierLastUsed() {
        viewModelScope.launch {
            try {
                val data = cashierRepository.getCashierLastUsed()
                if (data.isNotEmpty()) {
                    setCashierSelected(data[0].id, data[0].name)
                } else {
                    setCashierSelected("0", "Pemilik")
                }
            } catch (e: Exception) {
                Log.e("payment", e.message.toString())
                setCashierSelected("0", "Pemilik")
            }
        }
    }


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

    fun setCashierSelected(id: String, name: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(cashierId = id, cashierName = name)
        try {
            viewModelScope.launch {
                cashierRepository.setCashierIsLastUsed(id)
            }
        } catch (e: Exception) {
            Log.e("payment", e.message.toString())
        }
    }

    fun setCustomerSelected(id: String, name: String, numberPhone: String, note: String) {
        clearError()
        _stateUi.value =
            stateUi.value.copy(
                customerId = id,
                customerName = name,
                customerNumberPhone = numberPhone,
                customerNote = note
            )
    }

    fun setCustomerNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(customerNote = value)
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isFullPayment = value)
    }

    fun setIsOldCustomer(value: Boolean) {
        clearError()
        _isNewCustomerNameValid.value = ValidationResult(false, "")
        _isNewCustomerNumberPhoneValid.value = ValidationResult(false, "")
        _isOldCustomerValid.value = ValidationResult(false, "")

        if (value) {
            _stateUi.value = stateUi.value.copy(
                isOldCustomer = true,
                customerId = "",
                customerName = "Pilih Customer/Pelanggan",
                customerNote = "",
                customerNumberPhone = "",
            )
        } else {
            _stateUi.value = stateUi.value.copy(
                isOldCustomer = false,
                customerId = "",
                customerName = "",
                customerNote = "",
                customerNumberPhone = "",
            )
        }
    }

    fun setCustomerId(value: String) {
        clearError()
        _isOldCustomerValid.value = ValidationResult(false, "")
        _stateUi.value = _stateUi.value.copy(customerId = value)
    }

    fun setCustomerName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(customerName = value)
        if (stateUi.value.customerName.trim().isEmpty()) {
            _isNewCustomerNameValid.value =
                ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
        } else {
            _isNewCustomerNameValid.value = ValidationResult(false, "")
        }
    }

    fun setCustomerNumberPhone(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(customerNumberPhone = value)

        if (_stateUi.value.customerNumberPhone.trim().isEmpty()) {
            _isNewCustomerNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(_stateUi.value.customerNumberPhone.trim())) {
            _isNewCustomerNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        } else {
            _isNewCustomerNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun getCashier() {
        clearError()
        _stateListCashier.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashierRepository.getAllCashier()
                _stateListCashier.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListCashier.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun process(listDataProduct: List<ProductCart>) {
        try {
            viewModelScope.launch {
                //if new  customer
                if (!stateUi.value.isOldCustomer) {
                    val customerId = UUID.randomUUID().toString()
                    customerRepository.insert(
                        Customer(
                            id = customerId,
                            name = stateUi.value.customerName,
                            numberPhone = stateUi.value.customerNumberPhone,
                            note = stateUi.value.customerNote,
                            isDelete = false
                        )
                    )
                }

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
                    customerId = stateUi.value.customerId,
                    customerName = stateUi.value.customerName,
                    laundryStatusId = 1,
                    isFullPayment = stateUi.value.isFullPayment,
                    totalPrice = stateUi.value.totalPrice,
                    note = stateUi.value.note,
                    cashierId = stateUi.value.cashierId,
                    cashierName = stateUi.value.cashierName,
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
        //todo check custumer id tidak boleh 0
        //todo pembayaran ada pilihan customer baru atau lama

        if (stateUi.value.isOldCustomer) {
            if (stateUi.value.customerId == "") {
                _isOldCustomerValid.value = ValidationResult(true, "Silahkan Pilih Customer")
                _isProsesFailed.value = ValidationResult(true, "Silahkan Pilih Customer")
                return false
            }
        } else {
            if (stateUi.value.customerName.trim().isEmpty()) {
                _isNewCustomerNameValid.value =
                    ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
                _isProsesFailed.value = ValidationResult(true, "Nama Pelanggan Tidak Boleh Kosong")
                return false
            }
            if (_stateUi.value.customerNumberPhone.trim().isEmpty()) {
                _isNewCustomerNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
                _isProsesFailed.value = ValidationResult(true, "Nomor Harus Terisi")
                return false
            }
            if (!isNumberPhoneValid(_stateUi.value.customerNumberPhone.trim())) {
                _isNewCustomerNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
                _isProsesFailed.value = ValidationResult(true, "Nomor Belum Benar")
                return false
            }
        }

        if (stateUi.value.cashierId == "") {
            _isCashierValid.value = ValidationResult(true, "Silahkan Pilih Kasir")
            _isProsesFailed.value = ValidationResult(true, "Silahkan Pilih Kasir")
            return false
        }

        return true
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val transactionRepository: TransactionRepository,
    private val cashierRepository: CashierRepository,
    private val customerRepository: CustomerRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                cartRepository, transactionRepository, cashierRepository, customerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}