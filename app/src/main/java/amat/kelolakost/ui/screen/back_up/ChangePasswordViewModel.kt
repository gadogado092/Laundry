package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.data.repository.ChangePasswordRepository
import amat.kelolakost.generateMd5
import amat.kelolakost.ui.common.ValidationForm
import amat.kelolakost.ui.common.ValidationResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {

    private val repository = ChangePasswordRepository()

    private val _stateUi: MutableStateFlow<ChangePasswordUi> =
        MutableStateFlow(ChangePasswordUi())
    val stateUi: StateFlow<ChangePasswordUi>
        get() = _stateUi


    private val _isCurrentPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isCurrentPasswordValid: StateFlow<ValidationForm>
        get() = _isCurrentPasswordValid

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

    fun setCurrentPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(currentPassword = value)

        if (stateUi.value.currentPassword.trim().isEmpty()) {
            _isCurrentPasswordValid.value =
                ValidationForm(true, "Password Saat Ini Wajib Dimasukkan")
        } else {
            _isCurrentPasswordValid.value = ValidationForm(false, "")
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

        //check currentPassword
        if (stateUi.value.currentPassword.trim().isEmpty()) {
            _isCurrentPasswordValid.value =
                ValidationForm(true, "Password Saat Ini Wajib Dimasukkan")
            _isProsesValid.value = ValidationResult.Error("Password Saat Ini Wajib Dimasukkan")
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

    fun changePassword(token: String) {
        _isProsesValid.value = ValidationResult.Loading("Proses Ganti Password")

        viewModelScope.launch {
            try {
                val response =
                    repository.changePassword(
                        token,
                        generateMd5(stateUi.value.currentPassword.trim()),
                        generateMd5(stateUi.value.newPassword.trim())
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
                    ValidationResult.Error("Ganti Password Gagal " + e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesValid.value = ValidationResult.None
        _isRepeatNewPasswordValid.value = ValidationForm(false, "")
        _isCurrentPasswordValid.value = ValidationForm(false, "")
        _isNewPasswordValid.value = ValidationForm(false, "")
    }
}