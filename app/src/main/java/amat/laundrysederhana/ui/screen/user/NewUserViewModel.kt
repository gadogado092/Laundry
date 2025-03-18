package amat.laundrysederhana.ui.screen.user

import amat.laundrysederhana.addDateLimitApp
import amat.laundrysederhana.data.CashFlowCategory
import amat.laundrysederhana.data.Category
import amat.laundrysederhana.data.Customer
import amat.laundrysederhana.data.LaundryStatus
import amat.laundrysederhana.data.Product
import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.UserRepository
import amat.laundrysederhana.generateDateNow
import amat.laundrysederhana.generateDateTimeNow
import amat.laundrysederhana.isNumberPhoneValid
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
                32,
                "La-Undry",
                "Terima Kasih",
                "",
                50000,
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
            _isAddressValid.value = ValidationResult(true, "Alamat Wajib Dimasukkan")
            _isProsesSuccess.value = ValidationResult(true, "Alamat Wajib Dimasukkan")
            return
        }

        if (!_isBusinessNameValid.value.isError
            && !_isUserNumberPhoneValid.value.isError
            && !_isAddressValid.value.isError
        ) {
            viewModelScope.launch {
                val userId = UUID.randomUUID()
                val createAt = generateDateNow()
                val createDateTimeNow = generateDateTimeNow()
                val encodedDateTime = addDateLimitApp(createAt, "Bulan", 1)
                val key = UUID.randomUUID().toString().substring(0, 4).uppercase()

                val user =
                    _user.value.copy(
                        id = userId.toString(),
                        limit = encodedDateTime,
                        key = key,
                        createAt = createDateTimeNow
                    )

                val userList = listOf(
                    user,
                    User(
                        "0",
                        "Kosong",
                        "",
                        "",
                        "Standard",
                        "",
                        "",
                        32,
                        "La-Undry",
                        "Terima Kasih",
                        "",
                        50000,
                        "",
                        ""
                    ),

                    )

                insertNewUser(user = userList)
            }
        }

    }

    private suspend fun insertNewUser(user: List<User>) {
        try {

            val category1Id = UUID.randomUUID()
            val category2Id = UUID.randomUUID()

            val categoryList = listOf(
                Category(category1Id.toString(), "Kiloan", "Kg", false),
                Category(category2Id.toString(), "Satuan", "Pcs", false)
            )

            val productList = listOf(
                Product(
                    UUID.randomUUID().toString(),
                    "Cuci Lipat",
                    5000,
                    category1Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Cuci Lipat Expres",
                    7000,
                    category1Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Cuci Setrika",
                    7000,
                    category1Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Cuci Setrika Expres",
                    9000,
                    category1Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Bed Cover Besar",
                    40000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Bed Cover Kecil",
                    35000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Horden Tebal/Besar",
                    35000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Horden Sedang",
                    20000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Horden Kecil",
                    10000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Boneka Besar",
                    70000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Boneka Kecil",
                    50000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Selimut Besar",
                    25000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Selimut Kecil",
                    10000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Seprei Tebal",
                    30000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Seprei Sedang",
                    20000,
                    category2Id.toString(),
                    false
                ),
                Product(
                    UUID.randomUUID().toString(),
                    "Seprei Kecil",
                    10000,
                    category2Id.toString(),
                    false
                )
            )

            val statusList = listOf(
                LaundryStatus(1, "Proses"),
                LaundryStatus(2, "Siap Diambil"),
                LaundryStatus(3, "Selesai"),
                LaundryStatus(4, "Batal")
            )

            val cashFlowCategoryList = listOf(
                CashFlowCategory("0", "Tanpa Kategori", 1, "Buah", false),
                CashFlowCategory(UUID.randomUUID().toString(), "Sabun", 1, "ml", false),
                CashFlowCategory(UUID.randomUUID().toString(), "Parfum", 1, "ml", false),
                CashFlowCategory(UUID.randomUUID().toString(), "Gas", 1, "tabung", false)
            )

            val customer = Customer("0", "walk-in customer", "", false)

            userRepository.transactionInsertNewUser(
                user,
                statusList,
                categoryList,
                productList,
                customer,
                cashFlowCategoryList
            )

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