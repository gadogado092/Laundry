package amat.laundry.ui.screen.printer

import amat.laundry.data.User
import amat.laundry.data.entity.PrinterEntity
import amat.laundry.data.entity.ValidationResult
import amat.laundry.data.repository.UserRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PrinterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stateInitUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateInitUser: StateFlow<UiState<User>>
        get() = _stateInitUser

    private val _statePrinter: MutableStateFlow<UiState<List<PrinterEntity>>> =
        MutableStateFlow(UiState.Loading)
    val statePrinter: StateFlow<UiState<List<PrinterEntity>>>
        get() = _statePrinter

    private val _isProsesUpdatePrinterFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesUpdatePrinterFailed: StateFlow<ValidationResult>
        get() = _isProsesUpdatePrinterFailed

    init {
        val listPrint = mutableListOf<PrinterEntity>()
        updatePrinterList(listPrint)
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
                }
        }
    }

    fun updatePrinterSelected(userId: String, printer: PrinterEntity) {
        clearError()
        try {
            viewModelScope.launch {
                userRepository.printerSelected(
                    userId,
                    printerName = printer.name,
                    printerAddress = printer.address
                )
                getDetail()
                _isProsesUpdatePrinterFailed.value = ValidationResult(false)
            }
        } catch (e: Exception) {
            _isProsesUpdatePrinterFailed.value = ValidationResult(true, e.message.toString())
        }
    }

    fun updatePrinterList(listPrint: MutableList<PrinterEntity>) {
        clearError()
        _statePrinter.value = UiState.Success(listPrint)
    }

    private fun clearError() {
        _isProsesUpdatePrinterFailed.value = ValidationResult(true, "")
    }

}

class PrinterViewModelFactory(
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrinterViewModel::class.java)) {
            return PrinterViewModel(
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}