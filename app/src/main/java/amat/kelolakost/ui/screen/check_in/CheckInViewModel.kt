package amat.kelolakost.ui.screen.check_in

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenant
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
import amat.kelolakost.data.User
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.generateTextDuration
import amat.kelolakost.getLimitDay
import android.util.Log
import kotlinx.coroutines.flow.catch
import java.math.BigInteger
import java.util.UUID

class CheckInViewModel(
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val kostRepository: KostRepository,
    private val cashFlowRepository: CashFlowRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

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

    private val _isDownPaymentValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDownPaymentValid: StateFlow<ValidationResult>
        get() = _isDownPaymentValid

    private val _isNoteExtraPriceValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNoteExtraPriceValid: StateFlow<ValidationResult>
        get() = _isNoteExtraPriceValid

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
        getUser()
        Log.d("saya", "init")
        _checkInUi.value = checkInUi.value.copy(checkInDate = generateDateNow())
        _checkInUi.value = checkInUi.value.copy(createAt = generateDateNow())
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

    fun setUnitSelected(id: String, name: String, guaranteeCost: Int) {
        _isUnitSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value =
            _checkInUi.value.copy(unitId = id, unitName = name, guaranteeCost = guaranteeCost)
        _stateListUnit.value = UiState.Error("")
    }

    fun setPriceDurationSelected(price: String, duration: String) {
        _isDurationSelectedValid.value = ValidationResult(true, "")
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(price = price.toInt(), duration = duration)
        _stateListPriceDuration.value = UiState.Error("")

        refreshDataUI()
    }

    fun setExtraPrice(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(additionalCost = valueFormat)
        refreshDataUI()

        if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0 && checkInUi.value.noteAdditionalCost.isEmpty()) {
            _isCheckInSuccess.value = ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return
        } else {
            _isNoteExtraPriceValid.value = ValidationResult(false, "")
        }
    }

    fun setNoteExtraPrice(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        _checkInUi.value = _checkInUi.value.copy(noteAdditionalCost = value)
        if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0 && checkInUi.value.noteAdditionalCost.isEmpty()) {
            _isCheckInSuccess.value = ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return
        } else {
            _isNoteExtraPriceValid.value = ValidationResult(false, "")
        }
    }

    fun setDiscount(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(discount = valueFormat)
        refreshDataUI()
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
        setDownPayment("0")
    }

    fun setDownPayment(value: String) {
        clearError()
        _isCheckInSuccess.value = ValidationResult(true, "")
        _isDownPaymentValid.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _checkInUi.value = _checkInUi.value.copy(downPayment = valueFormat)

        if (checkInUi.value.downPayment.trim()
                .isEmpty() || checkInUi.value.downPayment.trim() == "0"
        ) {
            _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
        } else {
            _isDownPaymentValid.value = ValidationResult(false, "")
        }

        refreshDataUI()
    }

    fun setCheckInDate(value: String) {
        clearError()
        _checkInUi.value = checkInUi.value.copy(checkInDate = dateDialogToUniversalFormat(value))
        refreshDataUI()
    }

    fun setPaymentDate(value: String) {
        clearError()
        _checkInUi.value = checkInUi.value.copy(createAt = dateDialogToUniversalFormat(value))
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
                val data = unitRepository.getUnitByKost(
                    kostId = checkInUi.value.kostId,
                    unitStatusId = "2"
                )
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
        val extraPrice = cleanCurrencyFormatter(checkInUi.value.additionalCost)
        val discount = cleanCurrencyFormatter(checkInUi.value.discount)
        val qty = checkInUi.value.qty
        val price = checkInUi.value.price
        val priceGuarantee = checkInUi.value.guaranteeCost

        //HITUNG TOTAL BIAYA
        val total: BigInteger =
            (qty.toBigInteger() * price.toBigInteger()) + extraPrice.toBigInteger() + priceGuarantee.toBigInteger() - discount.toBigInteger()

        _checkInUi.value = checkInUi.value.copy(totalPrice = total.toString())

        if (checkInUi.value.isFullPayment) {
            _checkInUi.value = checkInUi.value.copy(totalPayment = total.toString())
        } else {
            val downPayment = cleanCurrencyFormatter(checkInUi.value.downPayment)
            val debtTenant: BigInteger = total - downPayment.toBigInteger()

            _checkInUi.value = checkInUi.value.copy(
                totalPayment = downPayment.toString(),
                debtTenant = debtTenant.toString()
            )
        }
    }

    fun processCheckIn() {
        clearError()

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

        //check Unit Selected
        if (checkInUi.value.unitId == "0") {
            _isUnitSelectedValid.value = ValidationResult(true, "Pilih Unit/Kamar Bro")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Unit/Kamar Bro")
            return
        }

        //check Price Selected
        if (checkInUi.value.price == 0) {
            _isDurationSelectedValid.value = ValidationResult(true, "Pilih Harga dan Durasi")
            _isCheckInSuccess.value = ValidationResult(true, "Pilih Harga dan Durasi")
            return
        }

        //check if extra price insert
        if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0 && checkInUi.value.noteAdditionalCost.isEmpty()) {
            _isCheckInSuccess.value = ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return
        }

        //check bayar cicil
        if (!checkInUi.value.isFullPayment) {
            if (checkInUi.value.downPayment.trim()
                    .isEmpty() || checkInUi.value.downPayment.trim() == "0"
            ) {
                _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
                _isCheckInSuccess.value = ValidationResult(true, "Masukkan Uang Muka")
                return
            }

            if (checkInUi.value.debtTenant.toBigInteger() < 1.toBigInteger()) {
                _isCheckInSuccess.value =
                    ValidationResult(true, "Pilih Metode Pembayaran LUNAS")
                return
            }
        }

        insertCheckIn()

    }

    private fun insertCheckIn() {
        try {
            viewModelScope.launch {
                val cashFlowid = UUID.randomUUID()

                var creditTenant = CreditTenant(
                    id = "0",
                    note = "",
                    tenantId = checkInUi.value.tenantId,
                    status = 0,
                    remainingDebt = cleanCurrencyFormatter(checkInUi.value.debtTenant),
                    kostId = checkInUi.value.kostId,
                    unitId = checkInUi.value.unitId,
                    createAt = checkInUi.value.createAt,
                    isDelete = false
                )

                var cashFlow = CashFlow(
                    id = cashFlowid.toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(checkInUi.value.totalPayment).toString(),
                    type = 0,
                    creditTenantId = "0",
                    creditId = "0",
                    debitId = "0",
                    unitId = checkInUi.value.unitId,
                    tenantId = checkInUi.value.tenantId,
                    kostId = checkInUi.value.kostId,
                    createAt = checkInUi.value.createAt,
                    isDelete = false
                )

                val qty = checkInUi.value.qty
                val price = checkInUi.value.price
                val totalPriceUnit = qty.toBigInteger() * price.toBigInteger()
                val durationText =
                    generateTextDuration(checkInUi.value.duration, checkInUi.value.qty)
                val checkInText = dateToDisplayMidFormat(checkInUi.value.checkInDate)
                val checkOutText = dateToDisplayMidFormat(checkInUi.value.checkOutDate)

                if (checkInUi.value.isFullPayment) {

                    var noteCashFlow =
                        "Pembayaran Lunas untuk penyewaan unit ${checkInUi.value.unitName}-${checkInUi.value.kostName} oleh ${checkInUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0) {
                        noteCashFlow += " + Biaya Tambahan ${checkInUi.value.additionalCost} (${checkInUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(checkInUi.value.discount) != 0) {
                        noteCashFlow += " - Diskon ${checkInUi.value.discount}"
                    }

                    if (checkInUi.value.guaranteeCost != 0) {
                        noteCashFlow += " - Uang Jaminan ${currencyFormatterStringViewZero(checkInUi.value.guaranteeCost.toString())}"
                    }

                    cashFlow = cashFlow.copy(note = noteCashFlow)

                } else {
                    val creditTenantId = UUID.randomUUID().toString()
                    cashFlow = cashFlow.copy(creditTenantId = creditTenantId)

                    var noteCashFlow =
                        "Pembayaran Uang Muka/DP untuk penyewaan unit ${checkInUi.value.unitName}-${checkInUi.value.kostName} oleh ${checkInUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0) {
                        noteCashFlow += " + Biaya Tambahan ${checkInUi.value.additionalCost} (${checkInUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(checkInUi.value.discount) != 0) {
                        noteCashFlow += " - Diskon ${checkInUi.value.discount}"
                    }

                    if (checkInUi.value.guaranteeCost != 0) {
                        noteCashFlow += " - Uang Jaminan ${currencyFormatterStringViewZero(checkInUi.value.guaranteeCost.toString())}"
                    }

                    noteCashFlow += "\nSisa tagihan ${currencyFormatterStringViewZero(checkInUi.value.debtTenant)}"

                    cashFlow = cashFlow.copy(note = noteCashFlow)

                    //NOTE HUTANG
                    var noteDebt =
                        "Pembayaran Uang Muka/DP untuk penyewaan unit ${checkInUi.value.unitName}-${checkInUi.value.kostName} oleh ${checkInUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(checkInUi.value.additionalCost) != 0) {
                        noteDebt += " + Biaya Tambahan ${checkInUi.value.additionalCost} (${checkInUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(checkInUi.value.discount) != 0) {
                        noteDebt += " - Diskon ${checkInUi.value.discount}"
                    }

                    if (checkInUi.value.guaranteeCost != 0) {
                        noteDebt += " - Uang Jaminan ${currencyFormatterStringViewZero(checkInUi.value.guaranteeCost.toString())}"
                    }

                    noteDebt += "\nSisa tagihan ${currencyFormatterStringViewZero(checkInUi.value.debtTenant)}"
                    creditTenant = creditTenant.copy(note = noteDebt, id = creditTenantId)

                }

                cashFlowRepository.insertCheckIn(
                    cashFlow = cashFlow,
                    creditTenant = creditTenant,
                    isFullPayment = checkInUi.value.isFullPayment,
                    limitCheckOut = checkInUi.value.checkOutDate,
                    additionalCost = cleanCurrencyFormatter(checkInUi.value.additionalCost),
                    noteAdditionalCost = checkInUi.value.noteAdditionalCost,
                    guaranteeCost = checkInUi.value.guaranteeCost
                )
            }
            _isCheckInSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isCheckInSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(user.value.limit)
            day.toInt() < 0
        } catch (e: Exception) {
            false
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
    private val kostRepository: KostRepository,
    private val cashFlowRepository: CashFlowRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckInViewModel::class.java)) {
            return CheckInViewModel(
                tenantRepository,
                unitRepository,
                kostRepository,
                cashFlowRepository,
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}