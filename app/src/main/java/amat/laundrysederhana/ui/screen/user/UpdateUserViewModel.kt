package amat.laundrysederhana.ui.screen.user

import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.UserRepository
import amat.laundrysederhana.isNumberPhoneValid
import amat.laundrysederhana.ui.common.UiState
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
        MutableStateFlow(User("", "", "", "", "", "", "", 32, "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

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

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun setTypeWa(value: String) {
        clearError()
        _user.value = _user.value.copy(typeWa = value)
    }

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
                val user = _user.value
                updateKost(user)
            }
        }
    }

    private suspend fun updateKost(user: User) {
        try {
            userRepository.updateUser(user)
            _isProsesSuccess.value = ValidationResult(false)
        } catch (e: Exception) {
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