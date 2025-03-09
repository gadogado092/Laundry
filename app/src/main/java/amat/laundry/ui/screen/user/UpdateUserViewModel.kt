package amat.laundry.ui.screen.user

import amat.laundry.data.User
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.UserRepository
import amat.laundry.isNumberPhoneValid
import amat.laundry.ui.common.UiState
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
        MutableStateFlow(User("", "", "", "", "", "", "", "", "","", "", 0, "", ""))
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

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun setTypeWa(value: String) {
        clearError()
        _user.value = _user.value.copy(typeWa = value)
    }

    fun setName(value: String) {
//        clearError()
//        _user.value = _user.value.copy(name = value)
//        if (_user.value.name.trim().isEmpty()) {
//            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
//        } else {
//            _isUserNameValid.value = ValidationResult(false, "")
//        }
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
//        clearError()
//        _user.value = _user.value.copy(email = value)
//
//        if (_user.value.email.trim().isEmpty()) {
//            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
//        } else if (!isEmailValid(_user.value.email.trim())) {
//            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
//        } else {
//            _isUserEmailValid.value = ValidationResult(false, "")
//        }
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
        clearError()
//        if (_user.value.name.trim().isEmpty()) {
//            _isUserNameValid.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
//            _isProsesSuccess.value = ValidationResult(true, "Nama Tidak Boleh Kosong")
//            return
//        }

        if (_user.value.numberPhone.trim().isEmpty()) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Harus Terisi")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Harus Terisi")
            return
        } else if (!isNumberPhoneValid(_user.value.numberPhone.trim())) {
            _isUserNumberPhoneValid.value = ValidationResult(true, "Nomor Belum Benar")
            _isProsesSuccess.value = ValidationResult(true, "Nomor Belum Benar")
            return
        }

//        if (_user.value.email.trim().isEmpty()) {
//            _isUserEmailValid.value = ValidationResult(true, "Email Wajib Dimasukkan")
//            _isProsesSuccess.value = ValidationResult(true, "Email Wajib Dimasukkan")
//            return
//        } else if (!isEmailValid(_user.value.email.trim())) {
//            _isUserEmailValid.value = ValidationResult(true, "Email Belum Valid")
//            _isProsesSuccess.value = ValidationResult(true, "Email Belum Valid")
//            return
//        }

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
        try {
            userRepository.updateUser(user)
            _isProsesSuccess.value = ValidationResult(false)
        }catch (e:Exception){
            _isProsesSuccess.value = ValidationResult(true, e.message.toString())
        }
    }

    fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
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