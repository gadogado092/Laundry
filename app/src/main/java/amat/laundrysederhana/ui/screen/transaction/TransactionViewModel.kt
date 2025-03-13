package amat.laundrysederhana.ui.screen.transaction

import amat.laundrysederhana.calenderSelect
import amat.laundrysederhana.data.TransactionLaundry
import amat.laundrysederhana.data.repository.TransactionRepository
import amat.laundrysederhana.dateDialogToRoomFormat
import amat.laundrysederhana.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    private val _stateUi: MutableStateFlow<TransactionUi> =
        MutableStateFlow(TransactionUi())
    val stateUi: StateFlow<TransactionUi>
        get() = _stateUi

    private val _stateListTransaction: MutableStateFlow<UiState<List<TransactionLaundry>>> =
        MutableStateFlow(UiState.Loading)
    val stateListTransaction: StateFlow<UiState<List<TransactionLaundry>>>
        get() = _stateListTransaction


    init {
        //end date
        val calendarEnd = Calendar.getInstance() // this takes current date
        calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)

        //start date
        val calendarStart = Calendar.getInstance()
        calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH) // get currentdate -7
        calendarStart.add(Calendar.DAY_OF_MONTH, -7)

        setInitDate(calendarStart.time, calendarEnd.time)

    }

    fun getTransaction() {
        _stateListTransaction.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = transactionRepository.getTransaction(
                    "${stateUi.value.startDate} 00:00:00",
                    "${stateUi.value.endDate} 23:59:59"
                )
                _stateListTransaction.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListTransaction.value = UiState.Error(e.message.toString())
            }
        }
    }

    private fun setInitDate(startDate: Date, dateEnd: Date) {
        _stateUi.value = stateUi.value.copy(
            startDate = calenderSelect(startDate),
            endDate = calenderSelect(dateEnd)
        )

    }

    fun setDateDialog(startDate: String, endDate: String) {
        _stateUi.value = stateUi.value.copy(
            startDate = dateDialogToRoomFormat(startDate),
            endDate = dateDialogToRoomFormat(endDate)
        )
        //todo get data
    }

}

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(
                transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}