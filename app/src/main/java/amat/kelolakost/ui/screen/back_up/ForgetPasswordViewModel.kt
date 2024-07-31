package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.checkIsEmailValid
import amat.kelolakost.data.repository.ForgetPasswordRepository
import amat.kelolakost.generateMd5
import amat.kelolakost.ui.common.ValidationForm
import amat.kelolakost.ui.common.ValidationResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgetPasswordViewModel : ViewModel() {

    private val repository = ForgetPasswordRepository()

    private val _stateUi: MutableStateFlow<ForgetPasswordUi> =
        MutableStateFlow(ForgetPasswordUi())
    val stateUi: StateFlow<ForgetPasswordUi>
        get() = _stateUi

    private val _isEmailValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isEmailValid: StateFlow<ValidationForm>
        get() = _isEmailValid

    private val _isCodeValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isCodeValid: StateFlow<ValidationForm>
        get() = _isCodeValid

    private val _isNewPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isNewPasswordValid: StateFlow<ValidationForm>
        get() = _isNewPasswordValid

    private val _isRepeatNewPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isRepeatNewPasswordValid: StateFlow<ValidationForm>
        get() = _isRepeatNewPasswordValid

    private val _isProsesValid: MutableStateFlow<ValidationResult<String>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesValid: StateFlow<ValidationResult<String>>
        get() = _isProsesValid

    private val _isProsesSendCodeValid: MutableStateFlow<ValidationResult<String>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesSendCodeValid: StateFlow<ValidationResult<String>>
        get() = _isProsesSendCodeValid

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

    fun setNewPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(newPassword = value)

        if (stateUi.value.newPassword.trim().isEmpty()) {
            _isNewPasswordValid.value =
                ValidationForm(true, "Password Baru Wajib Dimasukkan")
        } else {
            _isNewPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun setCode(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(code = value)

        if (stateUi.value.code.trim().isEmpty()) {
            _isNewPasswordValid.value =
                ValidationForm(true, "Kode Wajib Dimasukkan")
        } else {
            _isNewPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun setRepeatNewPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(repeatNewPassword = value)

        if (stateUi.value.repeatNewPassword.trim().isEmpty()) {
            _isRepeatNewPasswordValid.value =
                ValidationForm(true, "Password Baru Wajib Dimasukkan Kembali")
        } else if (stateUi.value.newPassword.trim() != stateUi.value.repeatNewPassword.trim()) {
            _isRepeatNewPasswordValid.value = ValidationForm(true, "Password Belum Sama")
        } else {
            _isRepeatNewPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()

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

        //check code
        if (stateUi.value.code.trim().isEmpty()) {
            _isCodeValid.value = ValidationForm(true, "Kode Wajib Dimasukkan")
            _isProsesValid.value = ValidationResult.Error("Kode Wajib Dimasukkan")
            return false
        }

        //check new password
        if (stateUi.value.newPassword.trim().isEmpty()) {
            _isNewPasswordValid.value = ValidationForm(true, "Password Baru Wajib Dimasukkan")
            _isProsesValid.value = ValidationResult.Error("Password Baru Wajib Dimasukkan")
            return false
        }

        //check repeat password
        if (stateUi.value.repeatNewPassword.trim().isEmpty()) {
            _isRepeatNewPasswordValid.value =
                ValidationForm(true, "Password Baru Wajib Dimasukkan Kembali")
            _isProsesValid.value = ValidationResult.Error("Password Baru Wajib Dimasukkan Kembali")
            return false
        } else if (stateUi.value.newPassword.trim() != stateUi.value.repeatNewPassword.trim()) {
            _isRepeatNewPasswordValid.value = ValidationForm(true, "Password Belum Sama")
            _isProsesValid.value = ValidationResult.Error("Password Belum Sama")
            return false
        }

        return true
    }

    fun sendCodeForgetPassword() {
        clearError()

        //check email
        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan")
            _isProsesSendCodeValid.value = ValidationResult.Error("Email Wajib Dimasukkan")
            return
        } else if (!checkIsEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationForm(true, "Email Belum Valid")
            _isProsesSendCodeValid.value = ValidationResult.Error("Email Belum Valid")
            return
        }

        _isProsesSendCodeValid.value = ValidationResult.Loading("Proses Kirim Kode")

        viewModelScope.launch {
            try {
                val response =
                    repository.sendCodeForgetPassword(
                        stateUi.value.email.trim()
                    )
                Log.d("mylog", response.toString())
                if (response.status) {
                    _isProsesSendCodeValid.value =
                        ValidationResult.Success(response.message)
                } else {
                    Log.e("mylog", response.message)
                    _isProsesSendCodeValid.value =
                        ValidationResult.Error(response.message)
                }

            } catch (e: Exception) {
                Log.e("mylog", e.toString())
                _isProsesSendCodeValid.value =
                    ValidationResult.Error("Kirim Kode Gagal " + e.message.toString())
            }
        }
    }

    fun forgetPassword() {
        clearError()
        _isProsesValid.value = ValidationResult.Loading("Proses Lupa Password")

        viewModelScope.launch {
            try {
                val response =
                    repository.forgetPassword(
                        stateUi.value.email.trim(),
                        generateMd5(stateUi.value.newPassword.trim()),
                        stateUi.value.code.trim()
                    )
                Log.d("mylog", response.toString())
                if (response.status) {
                    _isProsesValid.value =
                        ValidationResult.Success(response.message)
                } else {
                    Log.e("mylog", response.message)
                    _isProsesValid.value =
                        ValidationResult.Error(response.message)
                }

            } catch (e: Exception) {
                Log.e("mylog", e.toString())
                _isProsesValid.value =
                    ValidationResult.Error("Lupa Password Gagal " + e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesValid.value = ValidationResult.None
        _isProsesSendCodeValid.value = ValidationResult.None
        _isEmailValid.value = ValidationForm(false, "")
        _isRepeatNewPasswordValid.value = ValidationForm(false, "")
        _isCodeValid.value = ValidationForm(false, "")
        _isNewPasswordValid.value = ValidationForm(false, "")
    }
}