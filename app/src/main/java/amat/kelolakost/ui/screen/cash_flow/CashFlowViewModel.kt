package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.calenderSelect
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.dateDialogToRoomFormat
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    private val _stateBalance: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Loading)
    val stateBalance: StateFlow<UiState<String>>
        get() = _stateBalance

    private val _stateTotalIncome: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Loading)
    val stateTotalIncome: StateFlow<UiState<String>>
        get() = _stateTotalIncome

    private val _stateTotalOutcome: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Loading)
    val stateTotalOutcome: StateFlow<UiState<String>>
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

    fun setDateDialog(startDate: String, endDate: String) {
        _stateCashFLowUi.value = CashFLowUi(
            startDate = dateDialogToRoomFormat(startDate),
            endDate = dateDialogToRoomFormat(endDate)
        )
        getIncome()
        getOutCome()
    }

    fun getBalance() {
        _stateBalance.value = UiState.Loading
        viewModelScope.launch {
            cashFlowRepository.getBalanceFlow()
                .catch {
                    _stateBalance.value = UiState.Error(it.message.toString())
                }.collect { data ->
                    if (data.total.isNullOrEmpty()) {
                        _stateBalance.value = UiState.Success("0")
                    } else {
                        _stateBalance.value = UiState.Success(data.total!!)
                    }
                }
        }
    }

    fun getIncome() {
        viewModelScope.launch {
            cashFlowRepository.getTotalIncomeFlow(
                stateCashFLowUi.value.startDate,
                stateCashFLowUi.value.endDate
            ).catch {
                _stateTotalIncome.value = UiState.Error(it.message.toString())
            }.collect { data ->
                if (data.total.isNullOrEmpty()) {
                    _stateTotalIncome.value = UiState.Success("0")
                } else {
                    _stateTotalIncome.value = UiState.Success(data.total!!)
                }
            }
        }
    }

    fun getOutCome() {
        viewModelScope.launch {
            cashFlowRepository.getTotalOutcomeFlow(
                stateCashFLowUi.value.startDate,
                stateCashFLowUi.value.endDate
            ).catch {
                _stateTotalOutcome.value = UiState.Error(it.message.toString())
            }.collect { data ->
                if (data.total.isNullOrEmpty()) {
                    _stateTotalOutcome.value = UiState.Success("0")
                } else {
                    _stateTotalOutcome.value = UiState.Success(data.total!!)
                }
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