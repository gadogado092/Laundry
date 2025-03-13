package amat.laundrysederhana.ui.screen.other

import amat.laundrysederhana.addDateLimitApp
import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.UserRepository
import amat.laundrysederhana.generateDateNow
import amat.laundrysederhana.generateMd5
import amat.laundrysederhana.getLimitDay
import amat.laundrysederhana.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class OtherViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stateUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateUser: StateFlow<UiState<User>>
        get() = _stateUser

    private val _typeWa: MutableStateFlow<String> =
        MutableStateFlow("")
    val typeWa: StateFlow<String>
        get() = _typeWa

    private val _stateUi: MutableStateFlow<OtherUi> =
        MutableStateFlow(
            OtherUi(
                newLimit = addDateLimitApp(
                    generateDateNow(),
                    "Bulan",
                    1
                )
            )
        )
    val stateUi: StateFlow<OtherUi> get() = _stateUi

    private val _isProsesSuccess: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesSuccess: StateFlow<ValidationResult>
        get() = _isProsesSuccess

    fun getKostInit() {
        clearError()
        viewModelScope.launch {
            _stateUser.value = UiState.Loading
            val data = repository.getUser()
            _stateUi.value =
                stateUi.value.copy(
                    cost = data[0].cost,
                    limit = data[0].limit,
                    kode = data[0].key,
                    userId = data[0].id
                )
            _stateUser.value = UiState.Success(data[0])
            _typeWa.value = data[0].typeWa
            setNewLimit()
        }

    }

    private fun setNewLimit() {
        clearError()
        val day = getLimitDay(stateUi.value.limit).toInt()
        if (day < 0) {
            _stateUi.value = stateUi.value.copy(
                newLimit = addDateLimitApp(
                    generateDateNow(),
                    "Bulan",
                    stateUi.value.qty
                )
            )
        } else {
            _stateUi.value = stateUi.value.copy(
                newLimit = addDateLimitApp(
                    stateUi.value.limit,
                    "Bulan",
                    stateUi.value.qty
                )
            )
        }
    }

    fun addQuantity(value: String = "1") {
        clearError()
        val qty = stateUi.value.qty + value.toInt()
        if (stateUi.value.qty < 12) {
            _stateUi.value = stateUi.value.copy(qty = qty)
        }
        refreshUI()
    }

    fun minQuantity(value: String = "1") {
        clearError()
        val qty = stateUi.value.qty - value.toInt()
        if (stateUi.value.qty > 1) {
            _stateUi.value = stateUi.value.copy(qty = qty)
            refreshUI()
        }
    }

    fun setPassword(value: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(extendPassword = value)
    }

    private fun refreshUI() {
        setNewLimit()
    }

    fun proses() {
        clearError()

        if (stateUi.value.extendPassword.isEmpty()) {
            _isProsesSuccess.value = ValidationResult(true, "Masukkan Password")
            return
        }

        val passwordMd5 =
            generateMd5((stateUi.value.kode + stateUi.value.qty).lowercase()).substring(0, 4)
                .uppercase()
        Log.d("saya", passwordMd5)

        if (stateUi.value.extendPassword != passwordMd5) {
            _isProsesSuccess.value = ValidationResult(true, "Password Salah. Hubungi Cs")
            return
        }

        if (stateUi.value.extendPassword == passwordMd5) {
            try {
                viewModelScope.launch {
                    val key = UUID.randomUUID().toString().substring(0, 4).uppercase()

                    repository.extendApp(
                        userId = stateUi.value.userId,
                        newLimit = stateUi.value.newLimit,
                        newKey = key
                    )
                    _stateUi.value = stateUi.value.copy(qty = 1, extendPassword = "")
                    _isProsesSuccess.value = ValidationResult(false)
                }
            } catch (e: Exception) {
                _isProsesSuccess.value =
                    ValidationResult(true, "Gagal Perpanjang " + e.message.toString())
            }
        }

    }

    private fun clearError() {
        _isProsesSuccess.value = ValidationResult(true, "")
    }

}

class OtherViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherViewModel::class.java)) {
            return OtherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}