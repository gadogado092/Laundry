package amat.laundrysederhana.ui.screen.home

import amat.laundrysederhana.calenderSelect
import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.repository.CategoryRepository
import amat.laundrysederhana.data.repository.DetailTransactionRepository
import amat.laundrysederhana.data.repository.UserRepository
import amat.laundrysederhana.dateToEndTime
import amat.laundrysederhana.dateToStartTime
import amat.laundrysederhana.getLimitDay
import amat.laundrysederhana.ui.common.UiState
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
    private val detailTransactionRepository: DetailTransactionRepository
) : ViewModel() {

    private val _stateUser: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val stateUser: StateFlow<UiState<User>>
        get() = _stateUser

    private var _limitDay = ""

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

    fun getUserInit() {
        try {
            viewModelScope.launch {
                _stateUser.value = UiState.Loading
                val data = userRepository.getUser()
                _limitDay = data[0].limit
                _stateUser.value = UiState.Success(data[0])
            }
        } catch (e: Exception) {
            _stateUser.value = UiState.Error(e.message.toString())
        }

    }

    fun getDataTransaction() {
        try {
            viewModelScope.launch {
                _stateList.value = UiState.Loading
                val dataCategory = categoryRepository.getCategory()

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
                    containListMonth.add(
                        HomeItem(
                            categoryName = item.name,
                            categoryUnit = item.unit,
                            totalPrice = totalPriceMonth.total!!,
                            totalQty = totalQtyMonth.total!!
                        )
                    )

                }

                val dataList = HomeList(listMonth = containListMonth, listToday = containListToday)
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


}

class HomeViewModelFactory(
    private val repository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val detailTransactionRepository: DetailTransactionRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, categoryRepository, detailTransactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}
