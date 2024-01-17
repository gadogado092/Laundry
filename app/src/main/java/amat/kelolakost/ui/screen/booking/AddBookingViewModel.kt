package amat.kelolakost.ui.screen.booking

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.Booking
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.Kost
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.BookingRepository
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddBookingViewModel(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _stateUi: MutableStateFlow<BookingUi> =
        MutableStateFlow(
            BookingUi(
                kostId = "0",
                kostName = "Pilih Kost",
                unitId = "0",
                unitName = "Pilih Unit/Kamar"
            )
        )
    val stateUi: StateFlow<BookingUi>
        get() = _stateUi

    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    private val _stateListUnit: MutableStateFlow<UiState<List<UnitAdapter>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListUnit: StateFlow<UiState<List<UnitAdapter>>>
        get() = _stateListUnit

    private val _isKostSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostSelectedValid: StateFlow<ValidationResult>
        get() = _isKostSelectedValid

    private val _isUnitSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUnitSelectedValid: StateFlow<ValidationResult>
        get() = _isUnitSelectedValid

    private val _isNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNameValid: StateFlow<ValidationResult>
        get() = _isNameValid

    private val _isNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isNumberPhoneValid

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
            planCheckIn = addDateLimitApp(generateDateNow(), "Hari", 1)
        )
    }

    fun getKost() {
        clearError()

        viewModelScope.launch {
            _stateListKost.value = UiState.Loading
            try {
                val data = kostRepository.getAllKostOrder()
                _stateListKost.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListKost.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setKostSelected(id: String, name: String) {
        clearError()
        _isKostSelectedValid.value = ValidationResult(true, "")
        _stateUi.value = stateUi.value.copy(kostId = id, kostName = name)
    }

    fun getUnit() {
        clearError()

        //check Kost Selected
        if (stateUi.value.kostId == "0") {
            _isKostSelectedValid.value = ValidationResult(true, "Pilih Kost Gan")
            _isProsesSuccess.value = ValidationResult(true, "Pilih Kost Gan")
            return
        }

        viewModelScope.launch {
            _stateListUnit.value = UiState.Loading
            try {
                val data = unitRepository.getUnitAvailableBooking(
                    kostId = stateUi.value.kostId
                )
                _stateListUnit.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnit.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setName(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Masukkan Nama Gan")
        } else {
            _isNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(numberPhone = value)
        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Masukkan Nomor")
        } else {
            _isNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun setUnitSelected(id: String, name: String, unitTypeName: String) {
        clearError()
        _isUnitSelectedValid.value = ValidationResult(true, "")
        _stateUi.value =
            stateUi.value.copy(
                unitId = id,
                unitName = name,
                unitTypeName = unitTypeName
            )
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
    }

    fun setNote(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(note = value)
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _stateUi.value = stateUi.value.copy(isCash = value)
    }

    fun setPlanCheckIn(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(planCheckIn = dateDialogToUniversalFormat(value))
    }

    fun dataIsComplete(): Boolean {
        clearError()

        //check kost selected
        if (stateUi.value.kostId == "0") {
            _isKostSelectedValid.value = ValidationResult(true, "Pilih Kost Gan")
            _isProsesSuccess.value = ValidationResult(true, "Pilih Kost Gan")
            return false
        }

        //check kost unit selected
        if (stateUi.value.unitId == "0") {
            _isUnitSelectedValid.value = ValidationResult(true, "Pilih Unit/Kamar Gan")
            _isProsesSuccess.value = ValidationResult(true, "Pilih Unit/Kamar Gan")
            return false
        }

        //check name
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationResult(true, "Masukkan Nama Gan")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Nama Gan")
            return false
        }
        //check number phone
        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationResult(true, "Masukkan Nomor")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Nomor")
            return false
        }
        //Check Nominal
        if (cleanCurrencyFormatter(stateUi.value.nominal) < 1) {
            _isNominalValid.value = ValidationResult(true, "Masukkan Nominal")
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Nominal")
            return false
        }
        return true
    }

    fun process() {
        clearError()
        try {
            viewModelScope.launch {
                val booking = Booking(
                    id = UUID.randomUUID().toString(),
                    name = stateUi.value.name,
                    numberPhone = stateUi.value.numberPhone,
                    note = stateUi.value.note,
                    nominal = cleanCurrencyFormatter(stateUi.value.nominal).toString(),
                    planCheckIn = stateUi.value.planCheckIn,
                    unitId = stateUi.value.unitId,
                    kostId = stateUi.value.kostId,
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                val note =
                    "Pembayaran Booking pada kost ${stateUi.value.kostName} untuk unit ${stateUi.value.unitName}-${stateUi.value.unitTypeName} " +
                            "Oleh ${stateUi.value.name}(${stateUi.value.numberPhone})"
                val cashFlow = CashFlow(
                    id = UUID.randomUUID().toString(),
                    note = note,
                    nominal = cleanCurrencyFormatter(stateUi.value.nominal).toString(),
                    typePayment = if (stateUi.value.isCash) 1 else 0,
                    type = 0,
                    creditTenantId = "0",
                    creditId = "0",
                    debitId = "0",
                    unitId = stateUi.value.unitId,
                    tenantId = "0",
                    kostId = stateUi.value.kostId,
                    createAt = stateUi.value.createAt,
                    isDelete = false
                )

                bookingRepository.addBooking(booking, cashFlow)
                _isProsesSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    private fun clearError() {
        _stateListUnit.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _isProsesSuccess.value = ValidationResult(true, "")
    }


}

class AddBookingViewModelFactory(
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val bookingRepository: BookingRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddBookingViewModel::class.java)) {
            return AddBookingViewModel(
                unitRepository,
                kostRepository,
                bookingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}