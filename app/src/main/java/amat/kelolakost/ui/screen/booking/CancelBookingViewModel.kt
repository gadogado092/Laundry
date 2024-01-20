package amat.kelolakost.ui.screen.booking

import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.BookingHome
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.BookingRepository
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CancelBookingViewModel(private val repository: BookingRepository) : ViewModel() {
    private val _stateBooking: MutableStateFlow<UiState<BookingHome>> =
        MutableStateFlow(UiState.Loading)
    val stateBooking: StateFlow<UiState<BookingHome>>
        get() = _stateBooking

    private val _stateUi: MutableStateFlow<BookingUi> =
        MutableStateFlow(BookingUi())
    val stateUi: StateFlow<BookingUi>
        get() = _stateUi

    private val _isNominalValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNominalValid: StateFlow<ValidationResult>
        get() = _isNominalValid

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun getBooking(bookingId: String) {
        _stateBooking.value = UiState.Loading
        try {
            viewModelScope.launch {
                val data = repository.getBooking(bookingId)
                _stateUi.value = stateUi.value.copy(
                    id = data.id,
                    kostId = data.kostId,
                    kostName = data.kostName,
                    unitId = data.unitId,
                    unitName = data.unitName,
                    unitTypeName = data.unitTypeName,
                    name = data.name,
                    note = data.note,
                    numberPhone = data.numberPhone,
                    nominalBooking = data.nominal,
                    planCheckIn = data.planCheckIn
                )
                _stateBooking.value = UiState.Success(data)
            }
        } catch (e: Exception) {
            _stateBooking.value = UiState.Error(e.message.toString())
        }

    }

    fun dataIsComplete(): Boolean {
        clearError()

        if (cleanCurrencyFormatter(stateUi.value.nominal) > cleanCurrencyFormatter(stateUi.value.nominalBooking)) {
            _isNominalValid.value = ValidationResult(true, "Melebihi Uang Booking")
            _isProsesSuccess.value = ValidationResult(true, "Pengembalian Dana Melebihi Uang Booking")
            return false
        }

        return true
    }

    fun process() {
        clearError()
        try {
            viewModelScope.launch {
                val note =
                    "Pengembalian Dana Pembatalan Booking pada kost ${stateUi.value.kostName} untuk unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName} " +
                            "Oleh ${stateUi.value.name}(${stateUi.value.numberPhone})"
                val cashFlow = CashFlow(
                    id = UUID.randomUUID().toString(),
                    note = note,
                    nominal = cleanCurrencyFormatter(stateUi.value.nominal).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = 1,
                    creditTenantId = "0",
                    creditDebitId = "0",
                    unitId = stateUi.value.unitId,
                    tenantId = "0",
                    kostId = stateUi.value.kostId,
                    createAt = generateDateNow(),
                    isDelete = false
                )
                repository.cancelBooking(cashFlow, stateUi.value.id)
                _isProsesSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }
    }

    fun getData(): BookingUi {
        return stateUi.value
    }

    fun setNominal(value: String) {
        clearError()
        val valueFormat = currencyFormatterString(value)
        _stateUi.value = stateUi.value.copy(nominal = valueFormat)

        if (cleanCurrencyFormatter(stateUi.value.nominal) > cleanCurrencyFormatter(stateUi.value.nominalBooking)) {
            _isNominalValid.value = ValidationResult(true, "Melebihi Uang Booking")
        } else {
            _isNominalValid.value = ValidationResult(false, "")
        }

    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCash = value)
    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class CancelBookingViewModelFactory(private val repository: BookingRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CancelBookingViewModel::class.java)) {
            return CancelBookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}