package amat.laundry.ui.screen.user

import amat.laundry.addDateLimitApp
import amat.laundry.data.Category
import amat.laundry.data.LaundryStatus
import amat.laundry.data.Product
import amat.laundry.data.User
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.UserRepository
import amat.laundry.generateDateTimeNow
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
        MutableStateFlow(
            User(
                "",
                "",
                "",
                "",
                "Standard",
                "",
                "",
                "58 mm",
                "La-Undry",
                "Terima Kasih",
                "",
                30000,
                "",
                ""
            )
        )
    val user: StateFlow<User>
        get() = _user

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    private val _isBusinessNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isBusinessNameValid: StateFlow<ValidationResult>
        get() = _isBusinessNameValid

    private val _isUserNumberPhoneValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isUserNumberPhoneValid: StateFlow<ValidationResult>
        get() = _isUserNumberPhoneValid

    private val _isAddressValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isAddressValid: StateFlow<ValidationResult>
        get() = _isAddressValid

    private val _isKostNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostNameValid: StateFlow<ValidationResult>
        get() = _isKostNameValid

    fun setBusinessName(value: String) {
        clearError()
        _user.value = _user.value.copy(businessName = value)
        if (_user.value.businessName.trim().isEmpty()) {
            _isBusinessNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
        } else {
            _isBusinessNameValid.value = ValidationResult(false, "")
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

    fun setAddress(value: String) {
        clearError()
        _user.value = _user.value.copy(address = value)

        if (_user.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Alamat Wajib Dimasukkan")
        } else {
            _isAddressValid.value = ValidationResult(false, "")
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
        if (_user.value.businessName.trim().isEmpty()) {
            _isBusinessNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
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

        if (_user.value.address.trim().isEmpty()) {
            _isAddressValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
            _isProsesSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
            return
        }

        if (!_isBusinessNameValid.value.isError
            && !_isUserNumberPhoneValid.value.isError
            && !_isAddressValid.value.isError
            && !_isKostNameValid.value.isError
        ) {
            viewModelScope.launch {
                val userId = UUID.randomUUID()
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

            val category1Id = UUID.randomUUID()
            val category2Id = UUID.randomUUID()

            val categoryList = listOf(
                Category(category1Id.toString(), "Kiloan", "Kg", false),
                Category(category2Id.toString(), "Satuan", "Item", false)
            )

            val product1Id = UUID.randomUUID()
            val product2Id = UUID.randomUUID()
            val product3Id = UUID.randomUUID()

            val productList = listOf(
                Product(
                    product1Id.toString(),
                    "Laundry Komplit",
                    10000,
                    category1Id.toString(),
                    false
                ),
                Product(
                    product2Id.toString(),
                    "Laundry Kilat Komplit",
                    15000,
                    category1Id.toString(),
                    false
                ),
                Product(product3Id.toString(), "Karpet", 40000, category2Id.toString(), false),
                Product(product3Id.toString(), "Bed Cover", 30000, category2Id.toString(), false)
            )

            val statusList = listOf(
                LaundryStatus(1, "Proses"),
                LaundryStatus(2, "Siap Diambil"),
                LaundryStatus(3, "Selesai"),
                LaundryStatus(4, "Batal")
            )

            userRepository.transactionInsertNewUser(user, statusList, categoryList, productList)

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