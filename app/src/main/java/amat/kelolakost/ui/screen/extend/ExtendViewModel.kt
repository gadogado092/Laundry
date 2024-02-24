package amat.kelolakost.ui.screen.extend

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterString
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.UnitHome
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.BillEntity
import amat.kelolakost.data.entity.PriceDuration
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.dateDialogToUniversalFormat
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.generateDateNow
import amat.kelolakost.generateTextDuration
import amat.kelolakost.getLimitDay
import amat.kelolakost.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.UUID

class ExtendViewModel(
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _stateUnit: MutableStateFlow<UiState<UnitHome>> =
        MutableStateFlow(UiState.Loading)

    private val _extendUi: MutableStateFlow<ExtendUi> =
        MutableStateFlow(ExtendUi())
    val extendUi: StateFlow<ExtendUi>
        get() = _extendUi

    private val _isDurationSelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDurationSelectedValid: StateFlow<ValidationResult>
        get() = _isDurationSelectedValid

    private val _stateListPriceDuration: MutableStateFlow<UiState<List<PriceDuration>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListPriceDuration: StateFlow<UiState<List<PriceDuration>>>
        get() = _stateListPriceDuration

    private val _isNoteExtraPriceValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isNoteExtraPriceValid: StateFlow<ValidationResult>
        get() = _isNoteExtraPriceValid

    private val _isDownPaymentValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isDownPaymentValid: StateFlow<ValidationResult>
        get() = _isDownPaymentValid

    private val _isExtendSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))
    val isExtendSuccess: StateFlow<ValidationResult>
        get() = _isExtendSuccess

    private val _billEntity: MutableStateFlow<BillEntity> =
        MutableStateFlow(BillEntity())

    val billEntity: StateFlow<BillEntity>
        get() = _billEntity

    init {
        getUser()
        _extendUi.value = extendUi.value.copy(createAt = generateDateNow())
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

    fun getDetail(unitId: String, price: String, duration: String) {
        _stateUnit.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = unitRepository.getDetailUnit(unitId)
                _stateUnit.value = UiState.Success(data)

                val totalDebt = creditTenantRepository.getTotalDebt(data.tenantId)

                _extendUi.value = extendUi.value.copy(
                    unitId = data.id,
                    unitName = data.name,
                    unitTypeName = data.unitTypeName,
                    tenantId = data.tenantId,
                    tenantName = data.tenantName,
                    kostId = data.kostId,
                    kostName = data.kostName,
                    limitCheckOut = data.limitCheckOut,
                    additionalCost = currencyFormatterStringViewZero(data.additionalCost.toString()),
                    noteAdditionalCost = data.noteAdditionalCost,
                    currentDebtTenant = totalDebt,
                    tenantNumberPhone = data.tenantNumberPhone
                )

                if (price != "" && duration != "") {
                    setPriceDurationSelected(price, duration)
                }

            } catch (e: Exception) {
                _stateUnit.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

    fun setPriceDurationSelected(price: String, duration: String) {
        _isDurationSelectedValid.value = ValidationResult(true, "")
        _isExtendSuccess.value = ValidationResult(true, "")
        _extendUi.value = extendUi.value.copy(price = price.toInt(), duration = duration)
        _stateListPriceDuration.value = UiState.Error("")

        refreshDataUI()
    }

    fun setExtraPrice(value: String) {
        clearError()
        _isExtendSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _extendUi.value = extendUi.value.copy(additionalCost = valueFormat)
        refreshDataUI()

        if (cleanCurrencyFormatter(_extendUi.value.additionalCost) > 0 && _extendUi.value.noteAdditionalCost.isEmpty()) {
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return
        } else {
            _isNoteExtraPriceValid.value = ValidationResult(false, "")
        }
    }

    fun setNoteExtraPrice(value: String) {
        clearError()
        _isExtendSuccess.value = ValidationResult(true, "")
        _extendUi.value = extendUi.value.copy(noteAdditionalCost = value)
        if (cleanCurrencyFormatter(extendUi.value.additionalCost) > 0 && extendUi.value.noteAdditionalCost.isEmpty()) {
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return
        } else {
            _isNoteExtraPriceValid.value = ValidationResult(false, "")
        }
    }

    fun setDiscount(value: String) {
        clearError()
        _isExtendSuccess.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _extendUi.value = extendUi.value.copy(discount = valueFormat)
        refreshDataUI()
    }

    fun addQuantity(value: String = "1") {
        clearError()
        val qty = extendUi.value.qty + value.toInt()
        _extendUi.value = extendUi.value.copy(qty = qty)
        refreshDataUI()
    }

    fun minQuantity(value: String = "1") {
        clearError()
        val qty = extendUi.value.qty - value.toInt()
        if (extendUi.value.qty > 1) {
            _extendUi.value = extendUi.value.copy(qty = qty)
            refreshDataUI()
        }
    }

    fun setPaymentDate(value: String) {
        clearError()
        _extendUi.value = extendUi.value.copy(createAt = dateDialogToUniversalFormat(value))
        refreshDataUI()
    }


    fun dataIsComplete(): Boolean {
        clearError()
        if (cleanCurrencyFormatter(extendUi.value.additionalCost) > 0 && extendUi.value.noteAdditionalCost.isEmpty()) {
            _isExtendSuccess.value = ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            _isNoteExtraPriceValid.value =
                ValidationResult(true, "Masukkan Keterangan Biaya Tambahan")
            return false
        }

        //check bayar cicil
        if (!extendUi.value.isFullPayment) {
            if (extendUi.value.downPayment.trim()
                    .isEmpty() || extendUi.value.downPayment.trim() == "0"
            ) {
                _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
                _isExtendSuccess.value = ValidationResult(true, "Masukkan Uang Muka")
                return false
            }

            if (extendUi.value.debtTenantExtend.toBigInteger() < 1.toBigInteger()) {
                _isExtendSuccess.value =
                    ValidationResult(true, "Pilih Metode Pembayaran LUNAS")
                return false
            }
        }

        return true
    }

    fun prosesExtend() {
        clearError()
        try {
            viewModelScope.launch {
                _isExtendSuccess.value = ValidationResult(false)
                val cashFlowid = UUID.randomUUID()
                val createAt = generateDateNow()

                var creditTenant = CreditTenant(
                    id = "0",
                    note = "",
                    tenantId = extendUi.value.tenantId,
                    remainingDebt = cleanCurrencyFormatter(extendUi.value.debtTenantExtend),
                    kostId = extendUi.value.kostId,
                    unitId = extendUi.value.unitId,
                    createAt = createAt,
                    isDelete = false
                )

                var cashFlow = CashFlow(
                    id = cashFlowid.toString(),
                    note = "",
                    nominal = cleanCurrencyFormatter(extendUi.value.totalPayment).toString(),
                    typePayment = if (extendUi.value.isCash) 1 else 0,
                    type = 0,
                    creditTenantId = "0",
                    creditDebitId = "0",
                    unitId = extendUi.value.unitId,
                    tenantId = extendUi.value.tenantId,
                    kostId = extendUi.value.kostId,
                    createAt = createAt,
                    isDelete = false
                )

                val qty = extendUi.value.qty
                val price = extendUi.value.price
                val totalPriceUnit = qty.toBigInteger() * price.toBigInteger()
                val durationText =
                    generateTextDuration(extendUi.value.duration, extendUi.value.qty)
                val checkInText = dateToDisplayMidFormat(extendUi.value.limitCheckOut)
                val checkOutText = dateToDisplayMidFormat(extendUi.value.checkOutDateNew)

                if (extendUi.value.isFullPayment) {

                    var noteCashFlow =
                        "Pembayaran Lunas untuk Perpanjang unit ${extendUi.value.unitName}-${extendUi.value.kostName} oleh ${extendUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(extendUi.value.additionalCost) != 0) {
                        noteCashFlow += " + Biaya Tambahan ${extendUi.value.additionalCost} (${extendUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(extendUi.value.discount) != 0) {
                        noteCashFlow += " - Diskon ${extendUi.value.discount}"
                    }

                    cashFlow = cashFlow.copy(note = noteCashFlow)

                } else {
                    val creditTenantId = UUID.randomUUID().toString()
                    cashFlow = cashFlow.copy(creditTenantId = creditTenantId)

                    var noteCashFlow =
                        "Pembayaran Uang Muka/DP untuk Perpanjang unit ${extendUi.value.unitName}-${extendUi.value.kostName} oleh ${extendUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(extendUi.value.additionalCost) != 0) {
                        noteCashFlow += " + Biaya Tambahan ${extendUi.value.additionalCost} (${extendUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(extendUi.value.discount) != 0) {
                        noteCashFlow += " - Diskon ${extendUi.value.discount}"
                    }

                    noteCashFlow += " \nSisa tagihan ${currencyFormatterStringViewZero(extendUi.value.debtTenantExtend)}"

                    cashFlow = cashFlow.copy(note = noteCashFlow)

                    //NOTE HUTANG
                    var noteDebt =
                        "Piutang untuk Perpanjang unit ${extendUi.value.unitName}-${extendUi.value.kostName} oleh ${extendUi.value.tenantName}, " +
                                "selama ${durationText} (${checkInText} sampai ${checkOutText}) ${
                                    currencyFormatterStringViewZero(
                                        totalPriceUnit.toString()
                                    )
                                }"

                    if (cleanCurrencyFormatter(extendUi.value.additionalCost) != 0) {
                        noteDebt += " + Biaya Tambahan ${extendUi.value.additionalCost} (${extendUi.value.noteAdditionalCost})"
                    }

                    if (cleanCurrencyFormatter(extendUi.value.discount) != 0) {
                        noteDebt += " - Diskon ${extendUi.value.discount}"
                    }

                    creditTenant = creditTenant.copy(note = noteDebt, id = creditTenantId)

                }

                cashFlowRepository.prosesExtend(
                    cashFlow = cashFlow,
                    creditTenant = creditTenant,
                    isFullPayment = extendUi.value.isFullPayment,
                    limitCheckOut = extendUi.value.checkOutDateNew,
                    additionalCost = cleanCurrencyFormatter(extendUi.value.additionalCost),
                    noteAdditionalCost = extendUi.value.noteAdditionalCost
                )

                _billEntity.value = BillEntity(
                    kostName = extendUi.value.kostName,
                    createAt = dateToDisplayMidFormat(cashFlow.createAt),
                    nominal = currencyFormatterStringViewZero(cashFlow.nominal),
                    note = cashFlow.note,
                    typeWa = user.value.typeWa,
                    tenantNumberPhone = extendUi.value.tenantNumberPhone
                )

                Log.d("saya", "cashflow" + cashFlow.note)
                Log.d("saya", "bill" + _billEntity.value.note)

                _isExtendSuccess.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isExtendSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    fun setPaymentMethod(value: Boolean) {
        clearError()
        _extendUi.value = extendUi.value.copy(isFullPayment = value)
        setDownPayment("0")
    }

    fun setPaymentType(value: Boolean) {
        clearError()
        _extendUi.value = extendUi.value.copy(isCash = value)
    }

    fun setDownPayment(value: String) {
        clearError()
        _isExtendSuccess.value = ValidationResult(true, "")
        _isDownPaymentValid.value = ValidationResult(true, "")
        val valueFormat = currencyFormatterString(value)
        _extendUi.value = extendUi.value.copy(downPayment = valueFormat)

        if (extendUi.value.downPayment.trim()
                .isEmpty() || extendUi.value.downPayment.trim() == "0"
        ) {
            _isDownPaymentValid.value = ValidationResult(true, "Masukkan Uang Muka")
        } else {
            _isDownPaymentValid.value = ValidationResult(false, "")
        }

        refreshDataUI()
    }

    fun refreshDataUI() {
        //set New DateCheckOut
        if (extendUi.value.duration.isNotEmpty()) {
            _extendUi.value = extendUi.value.copy(
                checkOutDateNew = addDateLimitApp(
                    extendUi.value.limitCheckOut,
                    extendUi.value.duration,
                    extendUi.value.qty
                )
            )
        }

        val extraPrice = cleanCurrencyFormatter(extendUi.value.additionalCost)
        val discount = cleanCurrencyFormatter(extendUi.value.discount)
        val qty = extendUi.value.qty
        val price = extendUi.value.price

        //HITUNG TOTAL BIAYA
        val total: BigInteger =
            (qty.toBigInteger() * price.toBigInteger()) + extraPrice.toBigInteger() - discount.toBigInteger()

        _extendUi.value = extendUi.value.copy(totalPrice = total.toString())

        if (extendUi.value.isFullPayment) {
            _extendUi.value = extendUi.value.copy(totalPayment = total.toString())
        } else {
            val downPayment = cleanCurrencyFormatter(extendUi.value.downPayment)
            val currentDebtTenant =
                cleanCurrencyFormatter(extendUi.value.currentDebtTenant.toString())
            val debtTenant: BigInteger = total - downPayment.toBigInteger()
            val totalDebtTenant: BigInteger = debtTenant + currentDebtTenant.toBigInteger()

            _extendUi.value = extendUi.value.copy(
                totalPayment = downPayment.toString(),
                debtTenantExtend = debtTenant.toString(),
                totalDebtTenant = totalDebtTenant.toString()
            )
        }
    }

    fun clearError() {
        _isExtendSuccess.value = ValidationResult(true, "")
        _isDurationSelectedValid.value = ValidationResult(true, "")
    }

    fun getPriceDuration() {
        clearError()

        //check Unit Selected
        if (extendUi.value.unitId == "0") {
            _isExtendSuccess.value = ValidationResult(true, "Unit saat Ini tidak ada")
            return
        }

        viewModelScope.launch {
            _stateListPriceDuration.value = UiState.Loading
            try {
                val data = unitRepository.getPriceUnit(extendUi.value.unitId)
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

    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(user.value.limit)
            day.toInt() < 0
        } catch (e: Exception) {
            false
        }
    }

}

class ExtendViewModelModelFactory(
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val userRepository: UserRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExtendViewModel::class.java)) {
            return ExtendViewModel(
                unitRepository,
                creditTenantRepository,
                userRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}