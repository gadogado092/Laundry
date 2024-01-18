package amat.kelolakost.ui.screen.user

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.data.Booking
import amat.kelolakost.data.Credit
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.Debit
import amat.kelolakost.data.Kost
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitStatus
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.BookingRepository
import amat.kelolakost.data.repository.CreditRepository
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.data.repository.DebitRepository
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UnitStatusRepository
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.generateDateTimeNow
import amat.kelolakost.isEmailValid
import amat.kelolakost.isNumberPhoneValid
import android.net.Uri.encode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NewUserViewModel(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository,
    private val unitStatusRepository: UnitStatusRepository,
    private val unitTypeRepository: UnitTypeRepository,
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val creditRepository: CreditRepository,
    private val debitRepository: DebitRepository,
    private val bookingRepository: BookingRepository
) :
    ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "Standard", "", "", "", "", "", 25000, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _kost: MutableStateFlow<Kost> =
        MutableStateFlow(Kost("", "", "", "", "", false))
    val kost: StateFlow<Kost>
        get() = _kost

    private val _startToMain: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val startToMain: StateFlow<Boolean>
        get() = _startToMain

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
        _user.value = _user.value.copy(name = value)
        if (_user.value.name.trim().isEmpty()) {
            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isUserNameValid.value = ValidationResult(false, "")
        }
    }

    fun setNumberPhone(value: String) {
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
        _user.value = _user.value.copy(email = value)

        if (_user.value.email.trim().isEmpty()) {
            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
        } else if (!isEmailValid(_user.value.email.trim())) {
            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
        } else {
            _isUserEmailValid.value = ValidationResult(false, "")
        }
    }

    fun setKostName(value: String) {
        _kost.value = _kost.value.copy(name = value)
        if (_kost.value.name.trim().isEmpty()) {
            _isKostNameValid.value = ValidationResult(true, "Nama Kost Tidak Boleh Kosong")
        } else {
            _isKostNameValid.value = ValidationResult(false, "")
        }
    }

    fun setKostAddress(value: String) {
        _kost.value = _kost.value.copy(address = value)
        if (_kost.value.address.trim().isEmpty()) {
            _isKostAddressValid.value = ValidationResult(true, "Alamat Kost Tidak Boleh Kosong")
        } else {
            _isKostAddressValid.value = ValidationResult(false, "")
        }
    }

    fun setNote(value: String) {
        _kost.value = _kost.value.copy(note = value)
    }

    fun setTypeWa(value: String) {
        _user.value = _user.value.copy(typeWa = value)
    }

    fun setBankName(value: String) {
        _user.value = _user.value.copy(bankName = value)
    }

    fun setAccountNumber(value: String) {
        _user.value = _user.value.copy(accountNumber = value)
    }

    fun setAccountOwnerName(value: String) {
        _user.value = _user.value.copy(accountOwnerName = value)
    }

    fun setNoteBank(value: String) {
        _user.value = _user.value.copy(note = value)
    }

    fun prosesRegistration() {
        if (_user.value.name.trim().isEmpty()) {
            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        }

        if (_user.value.numberPhone.trim().isEmpty()) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
        } else if (!isNumberPhoneValid(_user.value.numberPhone.trim())) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
        }

        if (_user.value.email.trim().isEmpty()) {
            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
        } else if (!isEmailValid(_user.value.email.trim())) {
            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
        }

        if (_kost.value.name.trim().isEmpty()) {
            _isKostNameValid.value = ValidationResult(true, "Nama Kost Tidak Boleh Kosong")
        }

        if (_kost.value.address.trim().isEmpty()) {
            _isKostAddressValid.value = ValidationResult(true, "Alamat Kost Tidak Boleh Kosong")
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
                val encodedDateTime = encode(addDateLimitApp(createAt, "Bulan", 1))
                val key = UUID.randomUUID().toString().substring(0, 4).uppercase()

                val user =
                    _user.value.copy(
                        id = userId.toString(),
                        limit = encodedDateTime,
                        key = key,
                        createAt = createAt
                    )
                val kost = _kost.value.copy(id = kostId.toString(), createAt = createAt)
                insertNewUser(user = user, kost = kost)
            }
        }

    }

    private suspend fun insertNewUser(user: User, kost: Kost) {
        //insert data kosong
        try {
            val kostDummy =
                Kost(
                    id = "0",
                    name = "Tanpa Kost",
                    address = "",
                    note = "",
                    createAt = user.createAt,
                    isDelete = false
                )
            kostRepository.insertKost(kostDummy)

            val unitTypeDummy = UnitType(
                id = "0",
                name = "Tanpa Tipe Kamar/Unit",
                note = "",
                priceDay = 0,
                priceWeek = 0,
                priceMonth = 0,
                priceThreeMonth = 0,
                priceSixMonth = 0,
                priceYear = 0,
                priceGuarantee = 0,
                isDelete = false
            )
            unitTypeRepository.insertUnitType(unitTypeDummy)

            val tenantDummy = Tenant(
                id = "0",
                name = "Tanpa Penyewa",
                numberPhone = "",
                email = "",
                gender = false,
                address = "",
                note = "",
                limitCheckOut = "",
                additionalCost = 0,
                noteAdditionalCost = "",
                guaranteeCost = 0,
                unitId = "0",
                createAt = user.createAt,
                isDelete = false
            )
            tenantRepository.insertTenant(tenantDummy)

            val unitDummy = Unit(
                id = "0",
                name = "Tanpa Unit",
                note = "",
                noteMaintenance = "",
                unitTypeId = "0",
                unitStatusId = 2,
                tenantId = "0",
                kostId = "0",
                bookingId = "0",
                isDelete = false
            )
            unitRepository.insertUnit(unitDummy)

            //insert dummy creditTenant
            val creditTenant = CreditTenant(
                id = "0",
                note = "Kosong",
                tenantId = "0",
                remainingDebt = 0,
                kostId = "0",
                unitId = "0",
                createAt = user.createAt,
                isDelete = false
            )
            creditTenantRepository.insert(creditTenant)

            //insert dummy credit
            val credit = Credit(
                id = "0",
                note = "Kosong",
                status = -1,
                remaining = 0,
                customerCreditDebitId = "0",
                createAt = user.createAt,
                isDelete = false
            )
            creditRepository.insert(credit)

            //insert dummy debit
            val debit = Debit(
                id = "0",
                note = "Kosong",
                status = -1,
                remaining = 0,
                customerCreditDebitId = "0",
                createAt = user.createAt,
                isDelete = false
            )
            debitRepository.insert(debit)

            val booking = Booking(
                id = "0",
                name = "Kosong",
                numberPhone = "",
                note = "",
                nominal = "",
                planCheckIn = "",
                unitId = "0",
                kostId = "0",
                createAt = user.createAt,
                isDelete = false
            )
            bookingRepository.insert(booking)

            userRepository.insertUser(user)
            kostRepository.insertKost(kost)
            val listStatus = listOf(
                UnitStatus(1, "Terisi"),
                UnitStatus(2, "Kosong"),
                UnitStatus(3, "Pembersihan"),
                UnitStatus(4, "Perbaikan")
            )
            unitStatusRepository.insert(listStatus)

            _startToMain.value = true
        } catch (e: Exception) {
            _startToMain.value = false
        }

    }

}

class NewUserViewModelFactory(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository,
    private val unitStatusRepository: UnitStatusRepository,
    private val unitTypeRepository: UnitTypeRepository,
    private val tenantRepository: TenantRepository,
    private val unitRepository: UnitRepository,
    private val creditTenantRepository: CreditTenantRepository,
    private val creditRepository: CreditRepository,
    private val debitRepository: DebitRepository,
    private val bookingRepository: BookingRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewUserViewModel::class.java)) {
            return NewUserViewModel(
                userRepository,
                kostRepository,
                unitStatusRepository,
                unitTypeRepository,
                tenantRepository,
                unitRepository,
                creditTenantRepository,
                creditRepository,
                debitRepository,
                bookingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}