package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.calenderSelect
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.entity.Sum
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.data.response.ReportResponse
import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.dateDialogToRoomFormat
import amat.kelolakost.generateMd5
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.common.ValidationResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Date

class CashFlowViewModel(
    private val cashFlowRepository: CashFlowRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stateCashFLowUi: MutableStateFlow<CashFLowUi> =
        MutableStateFlow(CashFLowUi())
    val stateCashFLowUi: StateFlow<CashFLowUi>
        get() = _stateCashFLowUi

    private val _stateListCashFlow: MutableStateFlow<UiState<List<CashFlow>>> =
        MutableStateFlow(UiState.Loading)
    val stateListCashFlow: StateFlow<UiState<List<CashFlow>>>
        get() = _stateListCashFlow

    private val _stateReport: MutableStateFlow<ValidationResult<String>> =
        MutableStateFlow(ValidationResult.None)
    val stateReport: StateFlow<ValidationResult<String>>
        get() = _stateReport

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

    private val _typeWa: MutableStateFlow<String> =
        MutableStateFlow("")
    val typeWa: StateFlow<String>
        get() = _typeWa

    private val _userName: MutableStateFlow<String> =
        MutableStateFlow("")
    val userName: StateFlow<String>
        get() = _userName

    init {
        //start date
        val calendarStart = Calendar.getInstance()
        calendarStart[Calendar.DAY_OF_MONTH] = 1 // get tanggal 1

        //end date
        val calendarEnd = Calendar.getInstance() // this takes current date
        calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)

        setInitDate(calendarStart.time, calendarEnd.time)

        initUser()
    }

    private fun initUser() {
        viewModelScope.launch {
            val data = userRepository.getUser()
            _typeWa.value = data[0].typeWa
            _userName.value = data[0].name
        }

    }

    private fun setInitDate(startDate: Date, dateEnd: Date) {
        _stateCashFLowUi.value = stateCashFLowUi.value.copy(
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
        _stateCashFLowUi.value = stateCashFLowUi.value.copy(
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

    fun report() {
        _stateReport.value = ValidationResult.Loading("Loading Process Report...")
        viewModelScope.launch {

            try {

                //OUTCOME
                val dataOutCome = cashFlowRepository.getTotalOutcome(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )

                //INCOME
                val dataIncome = cashFlowRepository.getTotalIncome(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )

                //FOR ITEM DATA
                val data = cashFlowRepository.getAllCashFlow(
                    stateCashFLowUi.value.startDate,
                    stateCashFLowUi.value.endDate
                )

                val dataObject = JSONObject()
                val dataTransaction = JSONArray()

                data.forEach { item ->

                    var type = ""
                    if (item.type == 0) {
                        type = "Pemasukan"
                    } else if (item.type == 1) {
                        type = "Pengeluaran"
                    }

                    var paymentType = "-"
                    if (item.typePayment == 0) {
                        paymentType = "Transfer"
                    } else if (item.typePayment == 1) {
                        paymentType = "Tunai/Cash"
                    }

                    val data = JSONObject()
                    data.put(DATE_TRANSACTION, item.createAt)
                    data.put(DESC_TRANSACTION, item.note)
                    data.put(TYPE, type)
                    data.put(NOMINAL, item.nominal)
                    data.put(TYPE_PAYMENT, paymentType)
                    dataTransaction.put(data)
                }
                dataObject.put(TABLE_TRANSACTION, dataTransaction)


                //FOR HEADER
                val params = RequestParams()
                params.put("startDate", stateCashFLowUi.value.startDate)
                params.put("endDate", stateCashFLowUi.value.endDate)
                params.put(
                    "income",
                    if (dataIncome.total == null) "0" else dataIncome.total
                )
                params.put(
                    "outcome",
                    if (dataOutCome.total == null) "0" else dataOutCome.total
                )
                params.put("name", generateMd5(userName.value))
                params.put("type_user", TYPE_USER)
                params.put("data", dataObject)

                val client = AsyncHttpClient()
                val baseUrl = "http://apix.juragankost.id/api/"
                client.setTimeout(20000)
                client.post(
                    baseUrl + "offline/report/cash_flow",
                    params,
                    object : AsyncHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<out Header>,
                            responseBody: ByteArray
                        ) {
                            val result = String(responseBody)
                            Log.d("myLog", result)
                            val gson = Gson()
                            val response = gson.fromJson(result, ResponseModel::class.java)
                            if (response.status) {
                                val reportResponse =
                                    gson.fromJson(response.data, ReportResponse::class.java)
                                if (reportResponse.urlFile == "") {
                                    _stateReport.value =
                                        ValidationResult.Error(
                                            "Proses Report Excel Gagal Kode 100"
                                        )
                                } else {
                                    _stateReport.value =
                                        ValidationResult.Success(
                                            reportResponse.urlFile
                                        )
                                }
                            } else {
                                _stateReport.value =
                                    ValidationResult.Error(
                                        "Report Excel Gagal ${response.message}"
                                    )
                            }
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            responseBody: ByteArray?,
                            error: Throwable?
                        ) {
                            _stateReport.value =
                                ValidationResult.Error("Proses Report Excel Gagal ${error.toString()}")
                        }
                    })

            } catch (e: Exception) {
                _stateReport.value = ValidationResult.Error(e.message.toString())
            }
        }
    }

    fun resetReport() {
        _stateReport.value = ValidationResult.None
    }

    companion object {
        const val TABLE_TRANSACTION = "TABLE_TRANSACTION"
        const val DATE_TRANSACTION = "DATE_TRANSACTION"
        const val DESC_TRANSACTION = "DESC_TRANSACTION"
        const val TYPE = "TIPE"
        const val NOMINAL = "NOMINAL"
        const val TYPE_PAYMENT = "TIPE_PEMBAYARAN"

        const val TYPE_USER = "jkn"
    }

}

class CashFlowViewModelFactory(
    private val cashFlowRepository: CashFlowRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashFlowViewModel::class.java)) {
            return CashFlowViewModel(
                cashFlowRepository,
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}