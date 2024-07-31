package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.checkIsEmailValid
import amat.kelolakost.data.repository.LoginRepository
import amat.kelolakost.data.response.AccountBackupResponse
import amat.kelolakost.generateMd5
import amat.kelolakost.ui.common.ValidationForm
import amat.kelolakost.ui.common.ValidationResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    private val _stateUi: MutableStateFlow<UserUi> =
        MutableStateFlow(UserUi())
    val stateUi: StateFlow<UserUi>
        get() = _stateUi

    private val _isEmailValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isEmailValid: StateFlow<ValidationForm>
        get() = _isEmailValid

    private val _isPasswordValid: MutableStateFlow<ValidationForm> =
        MutableStateFlow(ValidationForm())

    val isPasswordValid: StateFlow<ValidationForm>
        get() = _isPasswordValid

    private val _isProsesValid: MutableStateFlow<ValidationResult<AccountBackupResponse>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesValid: StateFlow<ValidationResult<AccountBackupResponse>>
        get() = _isProsesValid

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

    fun setPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(password = value)

        if (stateUi.value.password.trim().isEmpty()) {
            _isPasswordValid.value = ValidationForm(true, "Password Wajib Dimasukkan")
        } else {
            _isPasswordValid.value = ValidationForm(false, "")
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()

        if (stateUi.value.email.trim().isEmpty()) {
            _isEmailValid.value = ValidationForm(true, "Email Wajib Dimasukkan")
            _isProsesValid.value = ValidationResult.Error("Email Wajib Dimasukkan")
            return false
        } else if (!checkIsEmailValid(stateUi.value.email.trim())) {
            _isEmailValid.value = ValidationForm(true, "Email Belum Valid")
            _isProsesValid.value = ValidationResult.Error("Email Belum Valid")
            return false
        }

        //check password
        if (stateUi.value.password.trim().isEmpty()) {
            _isPasswordValid.value = ValidationForm(true, "Masukkan Password Bro")
            _isProsesValid.value = ValidationResult.Error("Masukkan Password")
            return false
        }

        return true
    }

    fun login() {
        _isProsesValid.value = ValidationResult.Loading("Proses Login Pengguna")

        viewModelScope.launch {
            try {
                val gson = Gson()
                val response =
                    repository.login(stateUi.value.email.trim(), generateMd5(stateUi.value.password.trim()))
                Log.d("mylog", response.toString())
                if (response.status) {
                    val data = gson.fromJson(response.data, AccountBackupResponse::class.java)
                    Log.d("mylog", data.name)
                    _isProsesValid.value = ValidationResult.Success(data)
                } else {
                    Log.e("mylog", response.message)
                    _isProsesValid.value = ValidationResult.Error("Gagal " + response.message)
                }

            } catch (e: Exception) {
                Log.e("mylog", e.toString())
                _isProsesValid.value = ValidationResult.Error("Gagal " + e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesValid.value = ValidationResult.None
        _isEmailValid.value = ValidationForm(false, "")
        _isPasswordValid.value = ValidationForm(false, "")
    }
}