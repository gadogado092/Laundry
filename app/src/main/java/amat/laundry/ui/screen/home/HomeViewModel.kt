package amat.laundry.ui.screen.home

import amat.laundry.calenderSelect
import amat.laundry.data.User
import amat.laundry.data.repository.CashFlowCategoryRepository
import amat.laundry.data.repository.CashFlowRepository
import amat.laundry.data.repository.CategoryRepository
import amat.laundry.data.repository.DetailTransactionRepository
import amat.laundry.data.repository.UserRepository
import amat.laundry.dateToEndTime
import amat.laundry.dateToStartTime
import amat.laundry.getLimitDay
import amat.laundry.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HomeViewModel(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val detailTransactionRepository: DetailTransactionRepository,
    private val cashFlowCategoryRepository: CashFlowCategoryRepository,
    private val cashFlowRepository: CashFlowRepository
) : ViewModel() {

    private val _stateUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateUser: StateFlow<UiState<User>>
        get() = _stateUser

    private var _limitDay = ""
    private var _typeWa = ""

    private val _stateList: MutableStateFlow<UiState<HomeList>> =
        MutableStateFlow(UiState.Loading)
    val stateList: StateFlow<UiState<HomeList>>
        get() = _stateList

    private val _stateUi: MutableStateFlow<HomeUi> =
        MutableStateFlow(HomeUi())
    val stateUi: StateFlow<HomeUi>
        get() = _stateUi

    init {
        //start date
        val startDateMonth = Calendar.getInstance()
        startDateMonth[Calendar.DAY_OF_MONTH] = 1 // get tanggal 1

        //currentDate
        val currentDate = Calendar.getInstance()

        //end date
        val endDateMonth = Calendar.getInstance()
        endDateMonth[Calendar.DAY_OF_MONTH] =
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)

        setInitDate(
            currentDate = currentDate.time,
            startDateMonth = startDateMonth.time,
            endDateMonth = endDateMonth.time
        )

    }

    fun refresh() {
        getTotalBalance()
        getCashInDay()
        getCashOutDay()
        getCashOutMonth()
        getCashInMonth()
    }

    fun getUserInit() {
        try {
            viewModelScope.launch {
                _stateUser.value = UiState.Loading
                val data = userRepository.getUser()
                _limitDay = data[0].limit
                _typeWa = data[0].typeWa
                _stateUser.value = UiState.Success(data[0])
            }
        } catch (e: Exception) {
            _stateUser.value = UiState.Error(e.message.toString())
        }

    }

    fun getTotalBalance() {
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getBalanceNow()
                _stateUi.value = stateUi.value.copy(totalBalance = data)
            } catch (_: Exception) {
                _stateUi.value = stateUi.value.copy(totalBalance = "e")
            }
        }
    }

    fun getCashOutDay() {
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalOutcomeByDate(
                    dateToStartTime(stateUi.value.currentDate),
                    dateToEndTime(stateUi.value.currentDate)
                )
                _stateUi.value = stateUi.value.copy(totalCashOutDay = data)
            } catch (_: Exception) {
                _stateUi.value = stateUi.value.copy(totalCashOutDay = "e")
            }
        }
    }

    fun getCashOutMonth() {
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalOutcomeByDate(
                    dateToStartTime(stateUi.value.startDateMonth),
                    dateToEndTime(stateUi.value.endDateMonth)
                )
                _stateUi.value = stateUi.value.copy(totalCashOutMonth = data)
            } catch (_: Exception) {
                _stateUi.value = stateUi.value.copy(totalCashOutMonth = "e")
            }
        }
    }

    fun getCashInDay() {
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalIncomeByDate(
                    dateToStartTime(stateUi.value.currentDate),
                    dateToEndTime(stateUi.value.currentDate)
                )
                _stateUi.value = stateUi.value.copy(totalCashInDay = data)
            } catch (_: Exception) {
                _stateUi.value = stateUi.value.copy(totalCashInDay = "e")
            }
        }
    }

    fun getCashInMonth() {
        viewModelScope.launch {
            try {
                val data = cashFlowRepository.getTotalIncomeByDate(
                    dateToStartTime(stateUi.value.startDateMonth),
                    dateToEndTime(stateUi.value.endDateMonth)
                )
                _stateUi.value = stateUi.value.copy(totalCashInMonth = data)
            } catch (_: Exception) {
                _stateUi.value = stateUi.value.copy(totalCashInMonth = "e")
            }
        }
    }

    fun getDataTransaction() {
        try {
            viewModelScope.launch {
                _stateList.value = UiState.Loading
                val dataCategory = categoryRepository.getCategory()

                var totalTransactionToday = 0
                var totalTransactionMonth = 0
                val containListToday = mutableListOf<HomeItem>()
                val containListMonth = mutableListOf<HomeItem>()

                dataCategory.forEach { item ->
                    //get total price and total qty today
                    val totalPrice = detailTransactionRepository.getTotalPriceDetailTransaction(
                        item.id, dateToStartTime(stateUi.value.currentDate),
                        dateToEndTime(stateUi.value.currentDate)
                    )
                    if (totalPrice.total == null) {
                        totalPrice.total = "0"
                    }
                    val totalQty = detailTransactionRepository.getTotalQtyDetailTransaction(
                        item.id, dateToStartTime(stateUi.value.currentDate),
                        dateToEndTime(stateUi.value.currentDate)
                    )
                    if (totalQty.total == null) {
                        totalQty.total = "0"
                    }

                    totalTransactionToday += totalPrice.total!!.toInt()

                    containListToday.add(
                        HomeItem(
                            categoryName = item.name,
                            categoryUnit = item.unit,
                            totalPrice = totalPrice.total!!,
                            totalQty = totalQty.total!!
                        )
                    )

                    //get total price and total month
                    //get total price and total qty today
                    val totalPriceMonth =
                        detailTransactionRepository.getTotalPriceDetailTransaction(
                            item.id, dateToStartTime(stateUi.value.startDateMonth),
                            dateToEndTime(stateUi.value.endDateMonth)
                        )
                    if (totalPriceMonth.total == null) {
                        totalPriceMonth.total = "0"
                    }

                    val totalQtyMonth = detailTransactionRepository.getTotalQtyDetailTransaction(
                        item.id, dateToStartTime(stateUi.value.startDateMonth),
                        dateToEndTime(stateUi.value.endDateMonth)
                    )
                    if (totalQtyMonth.total == null) {
                        totalQtyMonth.total = "0"
                    }

                    totalTransactionMonth += totalPriceMonth.total!!.toInt()

                    containListMonth.add(
                        HomeItem(
                            categoryName = item.name,
                            categoryUnit = item.unit,
                            totalPrice = totalPriceMonth.total!!,
                            totalQty = totalQtyMonth.total!!
                        )
                    )

                }

                //cash flow area

                var totalCashFlowToday = 0
                var totalCashFlowMonth = 0

                val containCashFlowListToday = mutableListOf<HomeItem>()
                val containCashFlowListMonth = mutableListOf<HomeItem>()
                val dataCashFlowCategory = cashFlowCategoryRepository.getCategory()
                dataCashFlowCategory.forEach { item ->
                    //today
                    val totalPrice = cashFlowRepository.getTotalNominalCashFlow(
                        item.id, dateToStartTime(stateUi.value.currentDate),
                        dateToEndTime(stateUi.value.currentDate)
                    )
                    if (totalPrice.total == null) {
                        totalPrice.total = "0"
                    }

                    val totalQty = cashFlowRepository.getTotalQtyCashFlow(
                        item.id, dateToStartTime(stateUi.value.currentDate),
                        dateToEndTime(stateUi.value.currentDate)
                    )
                    if (totalQty.total == null) {
                        totalQty.total = "0"
                    }

                    totalCashFlowToday += totalPrice.total!!.toInt()

                    containCashFlowListToday.add(
                        HomeItem(
                            categoryName = item.name,
                            categoryUnit = item.unit,
                            totalPrice = totalPrice.total!!,
                            totalQty = totalQty.total!!,
                        )
                    )

                    //month
                    val totalPriceMonth = cashFlowRepository.getTotalNominalCashFlow(
                        item.id, dateToStartTime(stateUi.value.startDateMonth),
                        dateToEndTime(stateUi.value.endDateMonth)
                    )
                    if (totalPriceMonth.total == null) {
                        totalPriceMonth.total = "0"
                    }

                    val totalQtyMonth = cashFlowRepository.getTotalQtyCashFlow(
                        item.id, dateToStartTime(stateUi.value.startDateMonth),
                        dateToEndTime(stateUi.value.endDateMonth)
                    )
                    if (totalQtyMonth.total == null) {
                        totalQtyMonth.total = "0"
                    }

                    totalCashFlowMonth += totalPriceMonth.total!!.toInt()

                    containCashFlowListMonth.add(
                        HomeItem(
                            categoryName = item.name,
                            categoryUnit = item.unit,
                            totalPrice = totalPrice.total!!,
                            totalQty = totalQty.total!!,
                        )
                    )

                }


                val dataList = HomeList(
                    listMonth = containListMonth,
                    listToday = containListToday,
                    listCashFlowToday = containCashFlowListToday,
                    listCashFlowMonth = containCashFlowListMonth
                )
                _stateUi.value =
                    stateUi.value.copy(
                        totalTransactionToday = totalTransactionToday.toString(),
                        totalTransactionMonth = totalTransactionMonth.toString(),
                        totalCashFlowToday = totalCashFlowToday.toString(),
                        totalCashFlowMonth = totalCashFlowMonth.toString()
                    )
                _stateList.value = UiState.Success(dataList)
            }
        } catch (e: Exception) {
            _stateList.value = UiState.Error(e.message.toString())
        }
    }

    private fun setInitDate(currentDate: Date, startDateMonth: Date, endDateMonth: Date) {
        _stateUi.value = stateUi.value.copy(
            currentDate = calenderSelect(currentDate),
            startDateMonth = calenderSelect(startDateMonth),
            endDateMonth = calenderSelect(endDateMonth)
        )

    }

    fun checkLimitApp(): Boolean {
        return try {
            val day = getLimitDay(_limitDay)
            day.toInt() < 0
        } catch (e: Exception) {
            false
        }
    }

    fun getTypeWa(): String {
        return _typeWa
    }


}

class HomeViewModelFactory(
    private val repository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val detailTransactionRepository: DetailTransactionRepository,
    private val cashFlowCategoryRepository: CashFlowCategoryRepository,
    private val cashFlowRepository: CashFlowRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                repository,
                categoryRepository,
                detailTransactionRepository,
                cashFlowCategoryRepository,
                cashFlowRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}
