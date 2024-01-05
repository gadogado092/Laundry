package amat.kelolakost.ui.screen.check_in

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.data.Kost
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.ui.screen.unit.PriceDuration
import android.util.Log

class CheckInViewModel(
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository
) : ViewModel() {

    private val _checkInUi: MutableStateFlow<CheckInUi> =
        MutableStateFlow(
            CheckInUi(
                tenantId = "0",
                tenantName = "Pilih Penyewa",
                kostId = "0",
                kostName = "Pilih Kost",
                unitId = "0",
                unitName = "Pilih Unit/Kamar"
            )
        )
    val checkInUi: StateFlow<CheckInUi> get() = _checkInUi

    private val _isTenantSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isTenantSelectedValid: StateFlow<ValidationResult>
        get() = _isTenantSelectedValid

    private val _isKostSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostSelectedValid: StateFlow<ValidationResult>
        get() = _isKostSelectedValid

    private val _isUnitSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUnitSelectedValid: StateFlow<ValidationResult>
        get() = _isUnitSelectedValid

    private val _isDurationSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDurationSelectedValid: StateFlow<ValidationResult>
        get() = _isDurationSelectedValid

    private val _isCheckInSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isCheckInSuccess: StateFlow<ValidationResult>
        get() = _isCheckInSuccess

    private val _stateListTenant: MutableStateFlow<UiState<List<Tenant>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListTenant: StateFlow<UiState<List<Tenant>>>
        get() = _stateListTenant

    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    private val _stateListUnit: MutableStateFlow<UiState<List<UnitAdapter>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListUnit: StateFlow<UiState<List<UnitAdapter>>>
        get() = _stateListUnit

    private val _stateListPriceDuration: MutableStateFlow<UiState<List<PriceDuration>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListPriceDuration: StateFlow<UiState<List<PriceDuration>>>
        get() = _stateListPriceDuration

    init {
        Log.d("saya","init")
        _checkInUi.value = checkInUi.value.copy(checkInDate = generateDateNow())
    }

    fun setTenantSelected(id: String, name: String) {
        _isTenantSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(tenantId = id, tenantName = name)
        _stateListTenant.value = UiState.Error("")
    }

    fun setKostSelected(id: String, name: String) {
        _isKostSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(kostId = id, kostName = name)
        _stateListKost.value = UiState.Error("")
    }

    fun setUnitSelected(id: String, name: String) {
        _isUnitSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(unitId = id, unitName = name)
        _stateListUnit.value = UiState.Error("")
    }

    fun setPriceDurationSelected(price: String, duration: String) {
        _isUnitSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(price = price.toInt(), duration = duration)
        _stateListPriceDuration.value = UiState.Error("")

        refreshDataUI()
    }

    fun setPriceGuarantee(value: String) {
        _checkInUi.value = _checkInUi.value.copy(priceGuarantee = value.toInt())
    }

    fun setExtraPrice(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(extraPrice = valueFormat)
    }

    fun setNoteExtraPrice(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(noteExtraPrice = value)
    }

    fun setDiscount(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(discount = valueFormat)
    }

    fun addQuantity(value: String = "1") {
        clearError()
        val qty = _checkInUi.value.qty + value.toInt()
        _checkInUi.value = _checkInUi.value.copy(qty = qty)
        refreshDataUI()
    }

    fun minQuantity(value: String = "1") {
        clearError()
        val qty = _checkInUi.value.qty - value.toInt()
        if (_checkInUi.value.qty > 1) {
            _checkInUi.value = _checkInUi.value.copy(qty = qty)
            refreshDataUI()
        }
    }

    fun setPaymentMethod(value: Boolean) {
        clearError()
        _checkInUi.value = _checkInUi.value.copy(isFullPayment = value)
    }

    fun setDownPayment(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(downPayment = valueFormat)
    }

    fun setCheckInDate(value: String) {
        clearError()
        _checkInUi.value = checkInUi.value.copy(checkInDate = dateDialogToUniversalFormat(value))
        refreshDataUI()
    }

    fun getTenant() {
        clearError()

        viewModelScope.launch {
            _stateListTenant.value = UiState.Loading
            try {
                val data = tenantRepository.getTenantCheckOut()
                _stateListTenant.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListTenant.value = UiState.Error(e.message.toString())
            }
        }

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

    fun getUnit() {
        clearError()

        //check Kost Selected
        if (checkInUi.value.kostId == "0") {
            _isKostSelectedValid.value = ValidationResult(true, "Pilih Kost Gan")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Kost Gan")
            return
        }

        viewModelScope.launch {
            _stateListUnit.value = UiState.Loading
            try {
                val data = unitRepository.getUnit("2")
                _stateListUnit.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnit.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getPriceDuration() {
        clearError()

        //check Unit Selected
        if (checkInUi.value.unitId == "0") {
            _isUnitSelectedValid.value = ValidationResult(true, "Pilih Unit Gan")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Unit Gan")
            return
        }

        viewModelScope.launch {
            _stateListPriceDuration.value = UiState.Loading
            try {
                val data = unitRepository.getPriceUnit(checkInUi.value.unitId)
                val listPriceDuration = mutableListOf<PriceDuration>()
                if (data.priceDay != 0) {
                    listPriceDuration.add(PriceDuration(data.priceDay.toString(), "Hari"))
                }
                if (data.priceWeek != 0) {
                    listPriceDuration.add(PriceDuration(data.priceWeek.toString(), "Minggu"))
                }
                if (data.priceMonth != 0) {
                    listPriceDuration.add(PriceDuration(data.priceMonth.toString(), "Bulan"))
                }
                if (data.priceThreeMonth != 0) {
                    listPriceDuration.add(PriceDuration(data.priceThreeMonth.toString(), "3 Bulan"))
                }
                if (data.priceSixMonth != 0) {
                    listPriceDuration.add(PriceDuration(data.priceSixMonth.toString(), "6 Bulan"))
                }
                if (data.priceYear != 0) {
                    listPriceDuration.add(PriceDuration(data.priceYear.toString(), "Tahun"))
                }
                _stateListPriceDuration.value = UiState.Success(listPriceDuration)
            } catch (e: Exception) {
                _stateListPriceDuration.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun refreshDataUI() {
        //setDateCheckOut
        Log.d("saya", "refresh ${checkInUi.value.duration}")
        if (checkInUi.value.duration.isNotEmpty()) {
            _checkInUi.value = checkInUi.value.copy(
                checkOutDate = addDateLimitApp(
                    checkInUi.value.checkInDate,
                    checkInUi.value.duration,
                    checkInUi.value.qty
                )
            )
        }

    }

    fun processCheckIn() {
        _stateListTenant.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _stateListUnit.value = UiState.Error("")

        //check Tenant Selected
        if (checkInUi.value.tenantId == "0") {
            _isTenantSelectedValid.value = ValidationResult(true, "Pilih Tenant/Penyewa Gan")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Tenant/Penyewa Gan")
            return
        }

        //check Kost Selected
        if (checkInUi.value.kostId == "0") {
            _isKostSelectedValid.value = ValidationResult(true, "Pilih Kost Gan")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Kost Gan")
            return
        }

        //check Kost Selected
        if (checkInUi.value.unitId == "0") {
            _isUnitSelectedValid.value = ValidationResult(true, "Pilih Unit/Kamar Bro")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Unit/Kamar Bro")
            return
        }
    }

    private fun clearError() {
        _stateListTenant.value = UiState.Error("")
        _stateListUnit.value = UiState.Error("")
        _stateListKost.value = UiState.Error("")
        _stateListPriceDuration.value = UiState.Error("")
        _isCheckInSuccess.value = ValidationResult(true, "")
    }

}

class CheckInViewModelFactory(
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckInViewModel::class.java)) {
            return CheckInViewModel(tenantRepository, unitRepository, kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}