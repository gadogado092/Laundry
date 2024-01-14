package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.calenderSelect
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.entity.Sum
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.dateDialogToRoomFormat
import amat.kelolakost.ui.common.UiState
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

    private val _stateCashFLowUi: MutableStateFlow<CashFLowUi> =
        MutableStateFlow(CashFLowUi())
    val stateCashFLowUi: StateFlow<CashFLowUi>
        get() = _stateCashFLowUi

    private val _stateListCashFlow: MutableStateFlow<UiState<List<CashFlow>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCashFlow: StateFlow<UiState<List<CashFlow>>>
        get() = _stateListCashFlow

    private val _stateBalance: MutableStateFlow<UiState<Sum>> =
        MutableStateFlow(UiState.Loading)
    val stateBalance: StateFlow<UiState<Sum>>
        get() = _stateBalance

    private val _stateTotalIncome: MutableStateFlow<UiState<Sum>> =
        MutableStateFlow(UiState.Loading)
    val stateTotalIncome: StateFlow<UiState<Sum>>
        get() = _stateTotalIncome

    private val _stateTotalOutcome: MutableStateFlow<UiState<Sum>> =
        MutableStateFlow(UiState.Loading)
    val stateTotalOutcome: StateFlow<UiState<Sum>>
        get() = _stateTotalOutcome

    init {
        //start date
        val calendarStart = Calendar.getInstance()
        calendarStart[Calendar.DAY_OF_MONTH] = 1 // get tanggal 1

        //end date
        val calendarEnd = Calendar.getInstance() // this takes current date
        calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)

        setInitDate(calendarStart.time, calendarEnd.time)
    }

    fun setInitDate(startDate: Date, dateEnd: Date) {
        _stateCashFLowUi.value = CashFLowUi(
            startDate = calenderSelect(startDate),
            endDate = calenderSelect(dateEnd)
        )

    }

    fun getCashFlow() {
        _stateListCashFlow.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getAllCashFlow(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )
                _stateListCashFlow.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListCashFlow.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setDateDialog(startDate: String, endDate: String) {
        _stateCashFLowUi.value = CashFLowUi(
            startDate = dateDialogToRoomFormat(startDate),
            endDate = dateDialogToRoomFormat(endDate)
        )
        getIncome()
        getOutCome()
        getCashFlow()
    }

    fun getBalance() {
        _stateBalance.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getBalance()
                _stateBalance.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateBalance.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getIncome() {
        _stateTotalIncome.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalIncome(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )
                _stateTotalIncome.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateTotalIncome.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getOutCome() {
        _stateTotalOutcome.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalOutcome(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )
                _stateTotalOutcome.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateTotalOutcome.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class CashFlowViewModelFactory(
    private val cashFlowRepository: CashFlowRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashFlowViewModel::class.java)) {
            return CashFlowViewModel(cashFlowRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}