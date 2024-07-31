package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.checkIsEmailValid
import amat.kelolakost.checkIsNumberPhoneValid
import amat.kelolakost.data.repository.RegisterRepository
import amat.kelolakost.generateMd5
import amat.kelolakost.ui.common.ValidationForm
import amat.kelolakost.ui.common.ValidationResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    private val _stateUi: MutableStateFlow<RegisterUi> =
        MutableStateFlow(RegisterUi())
    val stateUi: StateFlow<RegisterUi>
        get() = _stateUi

    private val _isNameValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isNameValid: StateFlow<ValidationForm>
        get() = _isNameValid

    private val _isNumberPhoneValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isNumberPhoneValid: StateFlow<ValidationForm>
        get() = _isNumberPhoneValid

    private val _isEmailValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isEmailValid: StateFlow<ValidationForm>
        get() = _isEmailValid

    private val _isRepeatEmailValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isRepeatEmailValid: StateFlow<ValidationForm>
        get() = _isRepeatEmailValid

    private val _isPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isPasswordValid: StateFlow<ValidationForm>
        get() = _isPasswordValid

    private val _isRepeatPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isRepeatPasswordValid: StateFlow<ValidationForm>
        get() = _isRepeatPasswordValid

    private val _isProsesValid: MutableStateFlow<ValidationResult<String>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesValid: StateFlow<ValidationResult<String>>
        get() = _isProsesValid

    fun setName(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationForm(true, "Nama Tidak Boleh Kosong")
        } else {
            _isNameValid.value = ValidationForm(false, "")
        }
    }

    fun setNumberPhone(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(numberPhone = value)

        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationForm(true, "Nomor Harus Terisi")
        } else if (!checkIsNumberPhoneValid(stateUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationForm(true, "Format Nomor Belum Benar")
        } else {
            _isNumberPhoneValid.value = ValidationForm(false, "")
        }
    }

    fun setEmail(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(email = value)

        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan")
        } else if (!checkIsEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationForm(true, "Email Belum Valid")
        } else {
            _isEmailValid.value = ValidationForm(false, "")
        }
    }

    fun setRepeatEmail(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(repeatEmail = value)

        if (stateUi.value.repeatEmail.trim().isEmpty()) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan Kembali")
        } else if (!checkIsEmailValid(stateUi.value.repeatEmail.trim())) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Belum Valid")
        } else if (stateUi.value.email.trim() != stateUi.value.repeatEmail.trim()) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Belum Sama")
        } else {
            _isRepeatEmailValid.value = ValidationForm(false, "")
        }
    }

    fun setPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(password = value)

        if (stateUi.value.password.trim().isEmpty()) {
            _isPasswordValid.value = ValidationForm(true, "Password Wajib Dimasukkan")
        } else {
            _isPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun setRepeatPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(repeatPassword = value)

        if (stateUi.value.repeatPassword.trim().isEmpty()) {
            _isRepeatPasswordValid.value =
                ValidationForm(true, "Password Wajib Dimasukkan Kembali")
        } else if (stateUi.value.password.trim() != stateUi.value.repeatPassword.trim()) {
            _isRepeatPasswordValid.value = ValidationForm(true, "Password Belum Sama")
        } else {
            _isRepeatPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()

        //check name
        if (stateUi.value.name.trim().isEmpty()) {
            _isNameValid.value = ValidationForm(true, "Masukkan Nama")
            _isProsesValid.value = ValidationResult.Error("Masukkan Nama")
            return false
        }

        //check number phone
        if (stateUi.value.numberPhone.trim().isEmpty()) {
            _isNumberPhoneValid.value = ValidationForm(true, "Nomor Harus Terisi")
            _isProsesValid.value = ValidationResult.Error("Nomor Harus Terisi")
            return false
        } else if (!checkIsNumberPhoneValid(stateUi.value.numberPhone.trim())) {
            _isNumberPhoneValid.value = ValidationForm(true, "Format Nomor Belum Benar")
            _isProsesValid.value = ValidationResult.Error("Format Nomor Belum Benar")
            return false
        }

        //check email
        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan")
            _isProsesValid.value = ValidationResult.Error("Email Wajib Dimasukkan")
            return false
        } else if (!checkIsEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationForm(true, "Email Belum Valid")
            _isProsesValid.value = ValidationResult.Error("Email Belum Valid")
            return false
        }

        //check repeat email
        if (stateUi.value.repeatEmail.trim().isEmpty()) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan Kembali")
            _isProsesValid.value = ValidationResult.Error("Email Wajib Dimasukkan Kembali")
            return false
        } else if (!checkIsEmailValid(stateUi.value.repeatEmail.trim())) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Belum Valid")
            _isProsesValid.value = ValidationResult.Error("Email Belum Valid")
            return false
        } else if (stateUi.value.email.trim() != stateUi.value.repeatEmail.trim()) {
            _isRepeatEmailValid.value = ValidationForm(true, "Email Belum Sama")
            _isProsesValid.value = ValidationResult.Error("Email Belum Sama")
            return false
        }

        //check password
        if (stateUi.value.password.trim().isEmpty()) {
            _isPasswordValid.value = ValidationForm(true, "Masukkan Password Bro")
            _isProsesValid.value = ValidationResult.Error("Masukkan Password Bro")
            return false
        }

        //check repeat password
        if (stateUi.value.repeatPassword.trim().isEmpty()) {
            _isRepeatPasswordValid.value = ValidationForm(true, "Password Wajib Dimasukkan Kembali")
            _isProsesValid.value = ValidationResult.Error("Password Wajib Dimasukkan Kembali")
            return false
        } else if (stateUi.value.password.trim() != stateUi.value.repeatPassword.trim()) {
            _isRepeatPasswordValid.value = ValidationForm(true, "Password Belum Sama")
            _isProsesValid.value = ValidationResult.Error("Password Belum Sama")
            return false
        }

        return true
    }

    fun register() {
        _isProsesValid.value = ValidationResult.Loading("Proses Register Pengguna")

        viewModelScope.launch {
            try {
                val response =
                    repository.register(
                        stateUi.value.name.trim(),
                        stateUi.value.numberPhone.trim(),
                        stateUi.value.email.trim(),
                        generateMd5(stateUi.value.password.trim())
                    )
                Log.d("mylog", response.toString())
                if (response.status) {
                    _isProsesValid.value =
                        ValidationResult.Success("Pendaftaran ${stateUi.value.name} berhasil")
                } else {
                    Log.e("mylog", response.message)
                    _isProsesValid.value =
                        ValidationResult.Error("Register Gagal " + response.message)
                }

            } catch (e: Exception) {
                Log.e("mylog", e.toString())
                _isProsesValid.value =
                    ValidationResult.Error("Register Gagal " + e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesValid.value = ValidationResult.None
        _isNameValid.value = ValidationForm(false, "")
        _isNumberPhoneValid.value = ValidationForm(false, "")
        _isEmailValid.value = ValidationForm(false, "")
        _isRepeatEmailValid.value = ValidationForm(false, "")
        _isPasswordValid.value = ValidationForm(false, "")
        _isRepeatPasswordValid.value = ValidationForm(false, "")
    }
}