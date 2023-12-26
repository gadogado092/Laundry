package amat.kelolakost.ui.screen.user

import amat.kelolakost.addDateLimitApp
import amat.kelolakost.data.Kost
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.KostRepository
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

class UserViewModel(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository
) :
    ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "Standard", "", 25000, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _kost: MutableStateFlow<Kost> =
        MutableStateFlow(Kost("", "", "", "", ""))
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
        userRepository.insertUser(user)
        kostRepository.insertKost(kost)
        _startToMain.value = true
    }

}

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository, kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}