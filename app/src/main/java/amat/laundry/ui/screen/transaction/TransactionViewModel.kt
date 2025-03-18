package amat.laundry.ui.screen.transaction

import amat.laundry.calenderSelect
import amat.laundry.data.TransactionLaundry
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.dateDialogToRoomFormat
import amat.laundry.dateToEndTime
import amat.laundry.dateToStartTime
import amat.laundry.getLimitDay
import amat.laundry.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stateUi: MutableStateFlow<TransactionUi> =
        MutableStateFlow(TransactionUi())
    val stateUi: StateFlow<TransactionUi>
        get() = _stateUi

    private val _stateListTransaction: MutableStateFlow<UiState<List<TransactionLaundry>>> =
        MutableStateFlow(UiState.Loading)
    val stateListTransaction: StateFlow<UiState<List<TransactionLaundry>>>
        get() = _stateListTransaction

    private var _limitDay = ""


    init {
        //end date
        val calendarEnd = Calendar.getInstance() // this takes current date
        calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)

        //start date
        val calendarStart = Calendar.getInstance()
        calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH) // get currentdate -7
        calendarStart.add(Calendar.DAY_OF_MONTH, -7)

        setInitDate(calendarStart.time, calendarEnd.time)

        getUserInit()
    }

    private fun getUserInit() {
        try {
            viewModelScope.launch {
                val data = userRepository.getUser()
                _limitDay = data[0].limit
            }
        } catch (e: Exception) {
            Log.e("transaction", e.message.toString())
        }

    }

    fun getTransaction() {
        _stateListTransaction.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = transactionRepository.getTransaction(
                    dateToStartTime(stateUi.value.startDate),
                    dateToEndTime(stateUi.value.endDate)
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
        getTransaction()
    }

    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(_limitDay)
            day.toInt() < 0
        } catch (e: Exception) {
            false
        }
    }

}

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(
                transactionRepository, userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}