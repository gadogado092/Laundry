package amat.laundrysederhana.ui.screen.cashflow

import amat.laundrysederhana.calenderSelect
import amat.laundrysederhana.data.CashFlowAndCategory
import amat.laundrysederhana.data.repository.CashFlowRepository
import amat.laundrysederhana.dateDialogToRoomFormat
import amat.laundrysederhana.dateToEndTime
import amat.laundrysederhana.dateToStartTime
import amat.laundrysederhana.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class CashFlowViewModel(
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _stateUi: MutableStateFlow<CashFlowUi> =
        MutableStateFlow(CashFlowUi())
    val stateUi: StateFlow<CashFlowUi>
        get() = _stateUi

    private val _stateCashFlow: MutableStateFlow<UiState<List<CashFlowAndCategory>>> =
        MutableStateFlow(UiState.Loading)
    val stateCashFlow: StateFlow<UiState<List<CashFlowAndCategory>>>
        get() = _stateCashFlow

    init {
        //start date
        val calendarStart = Calendar.getInstance()
        calendarStart[Calendar.DAY_OF_MONTH] = 1 // get tanggal 1

        //end date
        val calendarEnd = Calendar.getInstance()
        calendarEnd[Calendar.DAY_OF_MONTH] =
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)

        setInitDate(calendarStart.time, calendarEnd.time)

        getCashFlowAndCategory()
    }

    fun getCashFlowAndCategory() {
        viewModelScope.launch {
            try {
                _stateCashFlow.value = UiState.Loading
                val data = cashFlowRepository.getCashFlowList(
                    dateToStartTime(stateUi.value.startDate),
                    dateToEndTime(stateUi.value.endDate)
                )
                _stateCashFlow.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateCashFlow.value = UiState.Error(e.message.toString())
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
        getCashFlowAndCategory()
    }
}

class CashFlowViewModelFactory(
    private val cashFlowRepository: CashFlowRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashFlowViewModel::class.java)) {
            return CashFlowViewModel(
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}