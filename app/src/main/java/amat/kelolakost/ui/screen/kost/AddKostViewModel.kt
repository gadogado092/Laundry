package amat.kelolakost.ui.screen.kost

import amat.kelolakost.data.Kost
import amat.kelolakost.data.entity.ValidationResult
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.generateDateTimeNow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddKostViewModel(private val kostRepository: KostRepository) : ViewModel() {
    private val _kost: MutableStateFlow<Kost> =
        MutableStateFlow(Kost("", "", "", "", ""))
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

    private val _isInsertSuccess: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isInsertSuccess: StateFlow<Boolean>
        get() = _isInsertSuccess

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

    fun prosesRegistration() {
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
                val kostId = UUID.randomUUID()
                val createAt = generateDateTimeNow()

                val kost = _kost.value.copy(id = kostId.toString(), createAt = createAt)
                insertKost(kost)
            }
        }
    }

    private suspend fun insertKost(kost: Kost) {
        kostRepository.insertKost(kost)
        _isInsertSuccess.value = true
    }
}

class AddKostViewModelFactory(
    private val kostRepository: KostRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddKostViewModel::class.java)) {
            return AddKostViewModel(kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}