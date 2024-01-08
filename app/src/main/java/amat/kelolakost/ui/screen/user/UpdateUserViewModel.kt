package amat.kelolakost.ui.screen.user

import amat.kelolakost.data.User
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.isEmailValid
import amat.kelolakost.isNumberPhoneValid
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UpdateUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stateInitUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateInitUser: StateFlow<UiState<User>>
        get() = _stateInitUser

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

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

    fun setTypeWa(value: String) {
        _user.value = _user.value.copy(typeWa = value)
    }

    private val _isUpdateSuccess: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean>
        get() = _isUpdateSuccess

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

    fun getDetail() {
        viewModelScope.launch {
            _stateInitUser.value = UiState.Loading
            userRepository.getDetail()
                .catch {
                    _stateInitUser.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateInitUser.value = UiState.Success(data)
                    _user.value = data
                }
        }
    }

    fun prosesUpdate() {
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

        if (!_isUserNameValid.value.isError
            && !_isUserNumberPhoneValid.value.isError
            && !_isUserEmailValid.value.isError
        ) {
            viewModelScope.launch {
                val user = _user.value
                updateKost(user)
            }
        }
    }

    private suspend fun updateKost(user: User) {
        userRepository.updateUser(user)
        _isUpdateSuccess.value = true
    }
}

class UpdateUserViewModelFactory(
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateUserViewModel::class.java)) {
            return UpdateUserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}