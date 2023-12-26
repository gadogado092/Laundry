package amat.kelolakost.ui.screen.kost

import amat.kelolakost.data.Kost
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UpdateKostViewModel(private val kostRepository: KostRepository) : ViewModel() {

    private val _stateInitKost: MutableStateFlow<UiState<Kost>> =
        MutableStateFlow(UiState.Loading)
    val stateInitKost: StateFlow<UiState<Kost>>
        get() = _stateInitKost

    private val _kost: MutableStateFlow<Kost> =
        MutableStateFlow(Kost("", "", "", "", "", false))
    val kost: StateFlow<Kost>
        get() = _kost

    private val _isKostNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostNameValid: StateFlow<ValidationResult>
        get() = _isKostNameValid

    private val _isKostAddressValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))
    val isKostAddressValid: StateFlow<ValidationResult>
        get() = _isKostAddressValid

    private val _isUpdateSuccess: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean>
        get() = _isUpdateSuccess

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

    fun getDetail(id: String) {
        viewModelScope.launch {
            _stateInitKost.value = UiState.Loading
            kostRepository.getDetail(id)
                .catch {
                    _stateInitKost.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateInitKost.value = UiState.Success(data)
                    _kost.value = data
                }
        }
    }

    fun prosesUpdate() {
        if (_kost.value.name.trim().isEmpty()) {
            _isKostNameValid.value = ValidationResult(true, "Nama Kost Tidak Boleh Kosong")
        }

        if (_kost.value.address.trim().isEmpty()) {
            _isKostAddressValid.value = ValidationResult(true, "Alamat Kost Tidak Boleh Kosong")
        }

        if (!_isKostNameValid.value.isError
            && !_isKostAddressValid.value.isError
        ) {
            viewModelScope.launch {
                val kost = _kost.value
                updateKost(kost)
            }
        }
    }

    private suspend fun updateKost(kost: Kost) {
        kostRepository.updateKost(kost)
        _isUpdateSuccess.value = true
    }
}

class UpdateKostViewModelFactory(
    private val kostRepository: KostRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateKostViewModel::class.java)) {
            return UpdateKostViewModel(kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}