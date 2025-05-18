package amat.laundry.ui.screen.home

import amat.laundry.calenderSelect
import amat.laundry.data.TransactionCustomer
import amat.laundry.data.User
import amat.laundry.data.entity.FilterEntity
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _listStatus = MutableStateFlow<List<FilterEntity>>(mutableListOf())
    val listStatus: StateFlow<List<FilterEntity>>
        get() = _listStatus

    private val _statusSelected = MutableStateFlow(FilterEntity("", ""))
    val statusSelected: StateFlow<FilterEntity>
        get() = _statusSelected

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", 32, 32, "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _stateUi: MutableStateFlow<HomeUi> =
        MutableStateFlow(HomeUi())
    val stateUi: StateFlow<HomeUi>
        get() = _stateUi

    private val _stateTransactionCustomer: MutableStateFlow<UiState<List<TransactionCustomer>>> =
        MutableStateFlow(UiState.Loading)
    val stateTransactionCustomer: StateFlow<UiState<List<TransactionCustomer>>>
        get() = _stateTransactionCustomer

    init {
        getUser()
        initData()
    }

    private fun initData() {
        val listStatus = listOf(
            FilterEntity("Siap Ambil", "1"),
            FilterEntity("Deadline", "2"),
            FilterEntity("Terlambat", "3")
        )

        _listStatus.value = listStatus
        _statusSelected.value = listStatus[0]

        //currentDate
        val currentDate = Calendar.getInstance()

        //tomorrow date
        val calendarTomorrow = Calendar.getInstance()
        calendarTomorrow.add(Calendar.DAY_OF_MONTH, +1)

        _stateUi.value = stateUi.value.copy(
            currentDate = calenderSelect(currentDate.time),
            tomorrowDate = calenderSelect(calendarTomorrow.time)
        )
    }

    fun refreshTotalStatus() {
        viewModelScope.launch {
            try {
                val readyToPickup = transactionRepository.getTotalReadyToPickUp()
                val late = transactionRepository.getTotalLate(stateUi.value.currentDate)
                val deadline = transactionRepository.getTotalDeadline(stateUi.value.tomorrowDate)

                val listStatus = listOf(
                    FilterEntity("Siap Ambil ($readyToPickup)", "1"),
                    FilterEntity("Deadline ($deadline)", "2"),
                    FilterEntity("Terlambat ($late)", "3")
                )

                _listStatus.value = listStatus

            } catch (_: Exception) {

            }
        }
    }

    fun updateStatusSelected(value: String) {
        _statusSelected.value = FilterEntity("", value)
        getTransaction()
    }

    private fun getUser() {
        viewModelScope.launch {
            userRepository.getDetail()
                .catch {

                }
                .collect { data ->
                    _user.value = data
                }
        }
    }

    fun getTransaction() {
        viewModelScope.launch {
            try {
                _stateTransactionCustomer.value = UiState.Loading
                if (statusSelected.value.value == "1") {
                    val data = transactionRepository.getDataReadyToPickUp()
                    _stateTransactionCustomer.value = UiState.Success(data)
                } else if (statusSelected.value.value == "2") {
                    val data = transactionRepository.getDataDeadLine(stateUi.value.tomorrowDate)
                    _stateTransactionCustomer.value = UiState.Success(data)
                } else if (statusSelected.value.value == "3") {
                    val data = transactionRepository.getDataLate(stateUi.value.currentDate)
                    _stateTransactionCustomer.value = UiState.Success(data)
                } else {
                    _stateTransactionCustomer.value = UiState.Error("Status Invalid")
                }

            } catch (e: Exception) {
                _stateTransactionCustomer.value = UiState.Error(e.message.toString())
            }
        }
    }

}

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
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