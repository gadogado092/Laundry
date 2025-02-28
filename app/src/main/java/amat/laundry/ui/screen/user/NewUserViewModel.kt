package amat.laundry.ui.screen.user

import amat.laundry.addDateLimitApp
import amat.laundry.data.User
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.UserRepository
import amat.laundry.generateDateTimeNow
import amat.laundry.isEmailValid
import amat.laundry.isNumberPhoneValid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NewUserViewModel(
    private val userRepository: UserRepository
) :
    ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "Standard", "", "", "", "", "", 25000, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    private val _isUserNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isUserNameValid: StateFlow<ValidationResult>
        get() = _isUserNameValid

    private val _isUserNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUserNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isUserNumberPhoneValid

    private val _isUserEmailValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUserEmailValid: StateFlow<ValidationResult>
        get() = _isUserEmailValid

    private val _isKostNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostNameValid: StateFlow<ValidationResult>
        get() = _isKostNameValid

    private val _isKostAddressValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostAddressValid: StateFlow<ValidationResult>
        get() = _isKostAddressValid

    fun setName(value: String) {
        clearError()
        _user.value = _user.value.copy(name = value)
        if (_user.value.name.trim().isEmpty()) {
            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isUserNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        clearError()
        _user.value = _user.value.copy(numberPhone = value)

        if (_user.value.numberPhone.trim().isEmpty()) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(_user.value.numberPhone.trim())) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        } else {
            _isUserNumberPhoneValid.value = ValidationResult(false, "")
        }
    }

    fun setEmail(value: String) {
        clearError()
        _user.value = _user.value.copy(email = value)

        if (_user.value.email.trim().isEmpty()) {
            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
        } else if (!isEmailValid(_user.value.email.trim())) {
            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
        } else {
            _isUserEmailValid.value = ValidationResult(false, "")
        }
    }

    fun setTypeWa(value: String) {
        clearError()
        _user.value = _user.value.copy(typeWa = value)
    }

    fun setFooterNote(value: String) {
        clearError()
        _user.value = _user.value.copy(footerNote = value)
    }

    fun prosesRegistration() {
        clearError()
        if (_user.value.name.trim().isEmpty()) {
            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            _isProsesSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
            return
        }

        if (_user.value.numberPhone.trim().isEmpty()) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Harus Terisi")
            return
        } else if (!isNumberPhoneValid(_user.value.numberPhone.trim())) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Belum Benar")
            return
        }

        if (_user.value.email.trim().isEmpty()) {
            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
            _isProsesSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
            return
        } else if (!isEmailValid(_user.value.email.trim())) {
            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
            _isProsesSuccess.value = ValidationResult(true, "Email Belum Valid")
            return
        }

        if (!_isUserNameValid.value.isError
            && !_isUserNumberPhoneValid.value.isError
            && !_isUserEmailValid.value.isError
            && !_isKostNameValid.value.isError
            && !_isKostAddressValid.value.isError
        ) {
            viewModelScope.launch {
                val userId = UUID.randomUUID()
                val kostId = UUID.randomUUID()
                val createAt = generateDateTimeNow()
                val encodedDateTime = addDateLimitApp(createAt, "Bulan", 1)
                val key = UUID.randomUUID().toString().substring(0, 4).uppercase()

                val user =
                    _user.value.copy(
                        id = userId.toString(),
                        limit = encodedDateTime,
                        key = key,
                        createAt = createAt
                    )

                insertNewUser(user = user)
            }
        }

    }

    private suspend fun insertNewUser(user: User) {
        try {
//            val kostDummy =
//                Kost(
//                    id = "0",
//                    name = "Tanpa Kost",
//                    address = "",
//                    note = "",
//                    createAt = user.createAt,
//                    isDelete = false
//                )
//            kostRepository.insertKost(kostDummy)
//
//            val unitTypeDummy = UnitType(
//                id = "0",
//                name = "Tanpa Tipe Kamar/Unit",
//                note = "",
//                priceDay = 0,
//                priceWeek = 0,
//                priceMonth = 0,
//                priceThreeMonth = 0,
//                priceSixMonth = 0,
//                priceYear = 0,
//                priceGuarantee = 0,
//                isDelete = false
//            )
//            unitTypeRepository.insertUnitType(unitTypeDummy)
//
//            val tenantDummy = Tenant(
//                id = "0",
//                name = "Tanpa Penyewa",
//                numberPhone = "",
//                email = "",
//                gender = false,
//                address = "",
//                note = "",
//                limitCheckOut = "",
//                additionalCost = 0,
//                noteAdditionalCost = "",
//                guaranteeCost = 0,
//                unitId = "0",
//                createAt = user.createAt,
//                isDelete = false
//            )
//            tenantRepository.insertTenant(tenantDummy)
//
//            val unitDummy = Unit(
//                id = "0",
//                name = "Tanpa Unit",
//                note = "",
//                noteMaintenance = "",
//                unitTypeId = "0",
//                unitStatusId = 2,
//                tenantId = "0",
//                kostId = "0",
//                bookingId = "0",
//                isDelete = false
//            )
//            unitRepository.insertUnit(unitDummy)
//
//            //insert dummy creditTenant
//            val creditTenant = CreditTenant(
//                id = "0",
//                note = "Kosong",
//                tenantId = "0",
//                remainingDebt = 0,
//                kostId = "0",
//                unitId = "0",
//                createAt = user.createAt,
//                isDelete = false
//            )
//            creditTenantRepository.insert(creditTenant)
//
//            //insert dummy credit debit
//            val creditDebit = CreditDebit(
//                id = "0",
//                note = "Kosong",
//                status = -1,
//                remaining = 0,
//                customerCreditDebitId = "0",
//                createAt = user.createAt,
//                dueDate = "",
//                isDelete = false
//            )
//            creditDebitRepository.insert(creditDebit)
//            //insert dummy credit debit customer
//            val customerCreditDebit = CustomerCreditDebit(
//                id = "0",
//                name = "Kosong",
//                numberPhone = "",
//                note = "",
//                email = "",
//                createAt = user.createAt,
//                isDelete = false
//            )
//            customerCreditDebitRepository.insert(customerCreditDebit)
//
//            val booking = Booking(
//                id = "0",
//                name = "Kosong",
//                numberPhone = "",
//                note = "",
//                nominal = "",
//                planCheckIn = "",
//                unitId = "0",
//                kostId = "0",
//                createAt = user.createAt,
//                isDelete = false
//            )
//            bookingRepository.insert(booking)
//
//            userRepository.insertUser(user)
//            kostRepository.insertKost(kost)
//            val listStatus = listOf(
//                UnitStatus(1, "Terisi"),
//                UnitStatus(2, "Kosong"),
//                UnitStatus(3, "Pembersihan"),
//                UnitStatus(4, "Perbaikan")
//            )
//            unitStatusRepository.insert(listStatus)

            _isProsesSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }

    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class NewUserViewModelFactory(
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewUserViewModel::class.java)) {
            return NewUserViewModel(
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}