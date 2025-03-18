package amat.laundry.ui.screen.category

import amat.laundry.data.Category
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.CategoryRepository
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddCategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _stateCategory: MutableStateFlow<UiState<Category>> =
        MutableStateFlow(UiState.Loading)
    val stateCategory: StateFlow<UiState<Category>>
        get() = _stateCategory

    private val _stateUi: MutableStateFlow<Category> =
        MutableStateFlow(Category(id = "", name = "", unit = "", isDelete = false))
    val stateUi: StateFlow<Category>
        get() = _stateUi

    private val _isCategoryNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isCategoryNameValid: StateFlow<ValidationResult>
        get() = _isCategoryNameValid

    private val _isUnitNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isUnitNameValid: StateFlow<ValidationResult>
        get() = _isUnitNameValid

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    fun getCategory(id: String) {
        if (id.isNotEmpty()) {
            clearError()
            viewModelScope.launch {
                try {
                    _stateCategory.value = UiState.Loading
                    val data = categoryRepository.getCategory(id)
                    _stateUi.value = data
                    _stateCategory.value = UiState.Success(data)
                } catch (e: Exception) {
                    _stateCategory.value = UiState.Error(e.message.toString())
                }
            }
        } else {
            _stateCategory.value = UiState.Success(stateUi.value)
        }
    }

    fun setCategoryName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isCategoryNameValid.value = ValidationResult(true, "Nama Category Tidak Boleh Kosong")
        } else {
            _isCategoryNameValid.value = ValidationResult(false, "")
        }
    }

    fun setUnitName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(unit = value)
        if (stateUi.value.unit.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Satuan/Unit Tidak Boleh Kosong")
        } else {
            _isUnitNameValid.value = ValidationResult(false, "")
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (stateUi.value.name.trim().isEmpty()) {
            _isCategoryNameValid.value = ValidationResult(true, "Nama Category Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nama Category Tidak Boleh Kosong")
            return false
        }

        if (stateUi.value.unit.trim().isEmpty()) {
            _isUnitNameValid.value = ValidationResult(true, "Nama Satuan/Unit Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nama Satuan/Unit Tidak Boleh Kosong")
            return false
        }

        return true
    }

    fun dataDeleteIsComplete(): Boolean {
        if (stateUi.value.id.isEmpty()) {
            _isProsesDeleteFailed.value = ValidationResult(true, "Id Data Tidak Ada")
            return false
        }
        return true
    }

    fun process() {
        try {
            viewModelScope.launch {
                if (stateUi.value.id.isNotEmpty()) {
                    categoryRepository.update(stateUi.value)
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val id = UUID.randomUUID().toString()
                    categoryRepository.insert(stateUi.value.copy(id = id))
                    _isProsesFailed.value = ValidationResult(false)
                }
            }
        } catch (e: Exception) {
            Log.e("bossku", e.message.toString())
            _isProsesFailed.value = ValidationResult(true, e.message.toString())
        }

    }

    fun processDelete() {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(id = stateUi.value.id)
                _isProsesDeleteFailed.value = ValidationResult(false)
            } catch (e: Exception) {
                Log.e("bossku", e.message.toString())
                _isProsesDeleteFailed.value = ValidationResult(true, e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesDeleteFailed.value = ValidationResult(true, "")
        _isProsesFailed.value = ValidationResult(true, "")
    }

}

class AddCategoryViewModelFactory(
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCategoryViewModel::class.java)) {
            return AddCategoryViewModel(
                categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}