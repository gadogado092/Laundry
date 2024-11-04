package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.data.Booking
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditDebit
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.data.Kost
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitStatus
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.User
import amat.kelolakost.data.repository.BackUpRepository
import amat.kelolakost.data.response.LastBackupResponse
import amat.kelolakost.data.response.OfflineResponse
import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.common.ValidationResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
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
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class BackUpViewModel(
    private val repository: BackUpRepository,
    private val accountBackupPreference: AccountBackupPreference
) : ViewModel() {

    private val _isProsesValid: MutableStateFlow<ValidationResult<String>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesValid: StateFlow<ValidationResult<String>>
        get() = _isProsesValid

    private val _isProsesRestoreValid: MutableStateFlow<ValidationResult<JSONObject>> =
        MutableStateFlow(ValidationResult.None)

    val isProsesRestoreValid: StateFlow<ValidationResult<JSONObject>>
        get() = _isProsesRestoreValid

    private val _isProsesCheckTokenValid: MutableStateFlow<ValidationResult<JSONObject>> =
        MutableStateFlow(ValidationResult.None)
    val isProsesCheckTokenValid: StateFlow<ValidationResult<JSONObject>>
        get() = _isProsesCheckTokenValid

    private val _stateUi: MutableStateFlow<BackUpUi> =
        MutableStateFlow(BackUpUi())
    val stateUi: StateFlow<BackUpUi>
        get() = _stateUi

    val typeUser = "jkn"
    val tableUser = "tableUser"
    private val columnId = "id"
    private val columnName = "name"
    private val columnNumberPhone = "numberPhone"
    private val columnEmail = "email"
    private val columnTypeWa = "typeWa"
    private val columnBankName = "bankName"
    private val columnAccountNumber = "accountNumber"
    private val columnAccountOwnerName = "accountOwnerName"
    private val columnNote = "note"
    private val columnLimit = "limit"
    private val columnCost = "cost"
    private val columnKey = "key"
    private val columnCreateAt = "createAt"

    val tableKost = "tableKost"
    private val columnAddress = "address"
    private val columnIsDelete = "isDelete"

    val tableUnitStatus = "tableUnitStatus"

    val tableUnitType = "tableUnitype"
    private val columnPriceDay = "priceDay"
    private val columnPriceWeek = "priceWeek"
    private val columnPriceMonth = "priceMonth"
    private val columnPriceThreeMonth = "priceThreeMonth"
    private val columnPriceSixMonth = "priceSixMonth"
    private val columnPriceYear = "priceYear"
    private val columnPriceGuarantee = "priceGuarantee"

    val tableUnit = "tableUnit"
    private val columnNoteMaintenance = "noteMaintenance"
    private val columnUnitTypeId = "unitTypeId"
    private val columnUnitStatusId = "unitStatusId"
    private val columnTenantId = "tenantId"
    private val columnKostId = "kostId"
    private val columnBookingId = "bookingId"

    val tableTenant = "tableTenant"
    private val columnGender = "gender"
    private val columnLimitCheckOut = "limitCheckOut"
    private val columnAdditionalCost = "additionalCost"
    private val columnNoteAdditionalCost = "noteAdditionalCost"
    private val columnGuaranteeCost = "guaranteeCost"
    private val columnUnitId = "unitId"

    val tableCashFlow = "tableCashFlow"
    private val columnNominal = "nominal"
    private val columnTypePayment = "typePayment"
    private val columnType = "type"
    private val columnCreditTenantId = "creditTenantId"
    private val columnCreditDebitId = "creditDebitId"

    val tableBooking = "tableBooking"
    private val columnPlanCheckIn = "planCheckIn"

    val tableCreditTenant = "tableCreditTenant"
    private val columnRemainingDebt = "remainingDebt"

    val tableCreditDebit = "tableCreditDebit"
    private val columnStatus = "status"
    private val columnRemaining = "remaining"
    private val columnCustomerCreditDebitId = "customerCreditDebitId"
    private val columnDueDate = "dueDate"

    val tableCustomerCreditDebit = "tableCustomerCreditDebit"

    init {
        val data = accountBackupPreference.getAccount()
        _stateUi.value = stateUi.value.copy(name = data.name, token = data.token, noWa = data.noWa)
    }

    fun backUp(context: Context) {
        clearError()
        _isProsesValid.value = ValidationResult.Loading("Loading Proses Backup")

        viewModelScope.launch {
            try {
                //Data User
                val dataUser = repository.getUser()
                val dataUserJson = JSONArray()
                dataUser.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNumberPhone, it.numberPhone)
                    data.put(columnEmail, it.email)
                    data.put(columnTypeWa, it.typeWa)
                    data.put(columnBankName, it.bankName)
                    data.put(columnAccountNumber, it.accountNumber)
                    data.put(columnAccountOwnerName, it.accountOwnerName)
                    data.put(columnNote, it.note)
                    data.put(columnLimit, it.limit)
                    data.put(columnCost, it.cost)
                    data.put(columnKey, it.key)
                    data.put(columnCreateAt, it.createAt)
                    dataUserJson.put(data)
                }

                //Data Kost
                val dataKost = repository.getKostList()
                val dataKostJson = JSONArray()
                dataKost.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnAddress, it.address)
                    data.put(columnNote, it.note)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataKostJson.put(data)
                }

                //Data UnitStatus
                val dataUnitStatus = repository.getUnitStatus()
                val dataUnitStatusJson = JSONArray()
                dataUnitStatus.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    dataUnitStatusJson.put(data)
                }

                //Data UnitType
                val dataUnitType = repository.getListUnitType()
                val dataUnitTypeJson = JSONArray()
                dataUnitType.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNote, it.note)
                    data.put(columnPriceDay, it.priceDay)
                    data.put(columnPriceWeek, it.priceWeek)
                    data.put(columnPriceMonth, it.priceMonth)
                    data.put(columnPriceThreeMonth, it.priceThreeMonth)
                    data.put(columnPriceSixMonth, it.priceSixMonth)
                    data.put(columnPriceYear, it.priceYear)
                    data.put(columnPriceGuarantee, it.priceGuarantee)
                    data.put(columnIsDelete, it.isDelete)
                    dataUnitTypeJson.put(data)
                }

                //Data Unit
                val dataUnit = repository.getListUnit()
                val dataUnitJson = JSONArray()
                dataUnit.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNote, it.note)
                    data.put(columnNoteMaintenance, it.noteMaintenance)
                    data.put(columnUnitTypeId, it.unitTypeId)
                    data.put(columnUnitStatusId, it.unitStatusId)
                    data.put(columnTenantId, it.tenantId)
                    data.put(columnKostId, it.kostId)
                    data.put(columnBookingId, it.bookingId)
                    data.put(columnIsDelete, it.isDelete)
                    dataUnitJson.put(data)
                }

                //Data Tenant
                val dataTenant = repository.getListTenant()
                val dataTenantJson = JSONArray()
                dataTenant.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNumberPhone, it.numberPhone)
                    data.put(columnEmail, it.email)
                    data.put(columnGender, it.gender)
                    data.put(columnAddress, it.address)
                    data.put(columnNote, it.note)
                    data.put(columnLimitCheckOut, it.limitCheckOut)
                    data.put(columnAdditionalCost, it.additionalCost)
                    data.put(columnNoteAdditionalCost, it.noteAdditionalCost)
                    data.put(columnGuaranteeCost, it.guaranteeCost)
                    data.put(columnUnitId, it.unitId)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataTenantJson.put(data)
                }

                //Data CashFlow
                val dataCashFlow = repository.getListCashFlow()
                val dataCashFlowJson = JSONArray()
                dataCashFlow.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnNote, it.note)
                    data.put(columnNominal, it.nominal)
                    data.put(columnTypePayment, it.typePayment)
                    data.put(columnType, it.type)
                    data.put(columnCreditTenantId, it.creditTenantId)
                    data.put(columnCreditDebitId, it.creditDebitId)
                    data.put(columnUnitId, it.unitId)
                    data.put(columnTenantId, it.tenantId)
                    data.put(columnKostId, it.kostId)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataCashFlowJson.put(data)
                }

                //Data Booking
                val dataBooking = repository.getListBooking()
                val dataBookingJson = JSONArray()
                dataBooking.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNumberPhone, it.numberPhone)
                    data.put(columnNote, it.note)
                    data.put(columnNominal, it.nominal)
                    data.put(columnPlanCheckIn, it.planCheckIn)
                    data.put(columnUnitId, it.unitId)
                    data.put(columnKostId, it.kostId)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataBookingJson.put(data)
                }

                //Data Credit Tenant
                val dataCreditTenant = repository.getListCreditTenant()
                val dataCreditTenantJson = JSONArray()
                dataCreditTenant.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnNote, it.note)
                    data.put(columnTenantId, it.tenantId)
                    data.put(columnRemainingDebt, it.remainingDebt)
                    data.put(columnKostId, it.kostId)
                    data.put(columnUnitId, it.unitId)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataCreditTenantJson.put(data)
                }

                //Data CreditDebit
                val dataCreditDebit = repository.getListCreditDebit()
                val dataCreditDebitJson = JSONArray()
                dataCreditDebit.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnNote, it.note)
                    data.put(columnStatus, it.status)
                    data.put(columnRemaining, it.remaining)
                    data.put(columnCustomerCreditDebitId, it.customerCreditDebitId)
                    data.put(columnDueDate, it.dueDate)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataCreditDebitJson.put(data)
                }

                //Data CustomerCreditDebit
                val dataCustomerCreditDebit = repository.getListCustomerCreditDebit()
                val dataCustomerCreditDebitJson = JSONArray()
                dataCustomerCreditDebit.forEach {
                    val data = JSONObject()
                    data.put(columnId, it.id)
                    data.put(columnName, it.name)
                    data.put(columnNumberPhone, it.numberPhone)
                    data.put(columnNote, it.note)
                    data.put(columnEmail, it.email)
                    data.put(columnCreateAt, it.createAt)
                    data.put(columnIsDelete, it.isDelete)
                    dataCustomerCreditDebitJson.put(data)
                }

                //Collect Data
                val dataCollect = JSONObject()
                dataCollect.put(tableUser, dataUserJson)
                dataCollect.put(tableKost, dataKostJson)
                dataCollect.put(tableUnitStatus, dataUnitStatusJson)
                dataCollect.put(tableUnitType, dataUnitTypeJson)
                dataCollect.put(tableUnit, dataUnitJson)
                dataCollect.put(tableTenant, dataTenantJson)
                dataCollect.put(tableCashFlow, dataCashFlowJson)
                dataCollect.put(tableBooking, dataBookingJson)
                dataCollect.put(tableCreditTenant, dataCreditTenantJson)
                dataCollect.put(tableCreditDebit, dataCreditDebitJson)
                dataCollect.put(tableCustomerCreditDebit, dataCustomerCreditDebitJson)

                //Create File
                val outputDir: File = context.cacheDir
                val outputFile = File.createTempFile("backup", ".txt", outputDir)
                val fileWriter = FileWriter(outputFile)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(dataCollect.toString())
                bufferedWriter.close()

                val params = RequestParams()
                params.put("back_up", outputFile)
                params.put("type_user", typeUser)

                val client = AsyncHttpClient()
                val baseUrl = "http://apix.juragankost.id/api/"
                client.addHeader("Authorization", stateUi.value.token)
                client.setTimeout(20000)
                client.post(
                    baseUrl + "offline/userks/backup",
                    params,
                    object : AsyncHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<out Header>,
                            responseBody: ByteArray
                        ) {
                            val result = String(responseBody)
                            val gson = Gson()
                            val response = gson.fromJson(result, ResponseModel::class.java)

                            if (response.status) {
                                _isProsesValid.value = ValidationResult.Success(response.message)
                            } else {
                                if (response.message == "Silahkan Login Kembali") {
                                    accountBackupPreference.logOut()
                                }
                                _isProsesValid.value =
                                    ValidationResult.Error(response.message)
                            }
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            responseBody: ByteArray?,
                            error: Throwable?
                        ) {
                            _isProsesValid.value =
                                ValidationResult.Error("Proses Backup Gagal ${error.toString()}")
                        }
                    })

            } catch (e: Exception) {
                Log.e("myloggg", e.message.toString())
                _isProsesValid.value =
                    ValidationResult.Error("Proses Backup Gagal Server Problem")
            }
        }
    }

    fun restore() {
        clearError()
        _isProsesRestoreValid.value = ValidationResult.Loading("Loading Data Restore")
        viewModelScope.launch {
            try {

                val params = RequestParams()
                params.put("type_user", typeUser)

                val client = AsyncHttpClient()
                val baseUrl = "http://apix.juragankost.id/api/"
                client.addHeader("Authorization", stateUi.value.token)
                client.setTimeout(20000)
                client.get(baseUrl + "offline/userks/restore", params,
                    object : AsyncHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<out Header>,
                            responseBody: ByteArray
                        ) {
                            val result = String(responseBody)
                            val gson = Gson()
                            val response = gson.fromJson(result, ResponseModel::class.java)
                            if (response.status) {
                                val restoreResponse =
                                    gson.fromJson(response.data, OfflineResponse::class.java)
                                val file = restoreResponse.file
                                val data = JSONObject(file)
                                _isProsesRestoreValid.value =
                                    ValidationResult.Success(data)
                            } else {
                                _isProsesRestoreValid.value =
                                    ValidationResult.Error("Loading Restore Gagal ${response.message}")
                            }
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            responseBody: ByteArray?,
                            error: Throwable?
                        ) {
                            _isProsesRestoreValid.value =
                                ValidationResult.Error("Loading Restore Gagal " + error.toString())
                        }

                    })
            } catch (e: Exception) {
                _isProsesRestoreValid.value =
                    ValidationResult.Error("Loading Restore Gagal " + e.message.toString())
            }
        }
    }

    fun insertDataRestore(data: JSONObject) {
        clearError()
        _isProsesValid.value = ValidationResult.Loading("Loading Insert Data Restore")
        viewModelScope.launch {
            try {

                val dataUser = data.getJSONArray(tableUser)
                val listUser = mutableListOf<User>()
                for (i in 0 until dataUser.length()) {
                    val jsonObject = dataUser.getJSONObject(i)
                    listUser.add(
                        User(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            numberPhone = jsonObject.getString(columnNumberPhone),
                            email = jsonObject.getString(columnEmail),
                            typeWa = jsonObject.getString(columnTypeWa),
                            bankName = jsonObject.getString(columnBankName),
                            accountNumber = jsonObject.getString(columnAccountNumber),
                            accountOwnerName = jsonObject.getString(columnAccountOwnerName),
                            note = jsonObject.getString(columnNote),
                            limit = jsonObject.getString(columnLimit),
                            cost = jsonObject.getInt(columnCost),
                            key = jsonObject.getString(columnKey),
                            createAt = jsonObject.getString(columnCreateAt)
                        )
                    )
                }

                val dataKost = data.getJSONArray(tableKost)
                val listKost = mutableListOf<Kost>()
                for (i in 0 until dataKost.length()) {
                    val jsonObject = dataKost.getJSONObject(i)
                    listKost.add(
                        Kost(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            address = jsonObject.getString(columnAddress),
                            note = jsonObject.getString(columnNote),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataUnitStatus = data.getJSONArray(tableUnitStatus)
                val listUnitStatus = mutableListOf<UnitStatus>()
                for (i in 0 until dataUnitStatus.length()) {
                    val jsonObject = dataUnitStatus.getJSONObject(i)
                    listUnitStatus.add(
                        UnitStatus(
                            id = jsonObject.getInt(columnId),
                            name = jsonObject.getString(columnName)
                        )
                    )
                }

                val dataUnitType = data.getJSONArray(tableUnitType)
                val listUnitType = mutableListOf<UnitType>()
                for (i in 0 until dataUnitType.length()) {
                    val jsonObject = dataUnitType.getJSONObject(i)
                    listUnitType.add(
                        UnitType(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            note = jsonObject.getString(columnNote),
                            priceDay = jsonObject.getInt(columnPriceDay),
                            priceWeek = jsonObject.getInt(columnPriceWeek),
                            priceMonth = jsonObject.getInt(columnPriceMonth),
                            priceThreeMonth = jsonObject.getInt(columnPriceThreeMonth),
                            priceSixMonth = jsonObject.getInt(columnPriceSixMonth),
                            priceYear = jsonObject.getInt(columnPriceYear),
                            priceGuarantee = jsonObject.getInt(columnPriceGuarantee),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataUnit = data.getJSONArray(tableUnit)
                val listUnit = mutableListOf<Unit>()
                for (i in 0 until dataUnit.length()) {
                    val jsonObject = dataUnit.getJSONObject(i)
                    listUnit.add(
                        Unit(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            note = jsonObject.getString(columnNote),
                            noteMaintenance = jsonObject.getString(columnNoteMaintenance),
                            unitTypeId = jsonObject.getString(columnUnitTypeId),
                            unitStatusId = jsonObject.getInt(columnUnitStatusId),
                            tenantId = jsonObject.getString(columnTenantId),
                            kostId = jsonObject.getString(columnKostId),
                            bookingId = jsonObject.getString(columnBookingId),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataTenant = data.getJSONArray(tableTenant)
                val listTenant = mutableListOf<Tenant>()
                for (i in 0 until dataTenant.length()) {
                    val jsonObject = dataTenant.getJSONObject(i)
                    listTenant.add(
                        Tenant(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            numberPhone = jsonObject.getString(columnNumberPhone),
                            email = jsonObject.getString(columnEmail),
                            gender = jsonObject.getBoolean(columnGender),
                            address = jsonObject.getString(columnAddress),
                            note = jsonObject.getString(columnNote),
                            limitCheckOut = jsonObject.getString(columnLimitCheckOut),
                            additionalCost = jsonObject.getInt(columnAdditionalCost),
                            noteAdditionalCost = jsonObject.getString(columnNoteAdditionalCost),
                            guaranteeCost = jsonObject.getInt(columnGuaranteeCost),
                            unitId = jsonObject.getString(columnUnitId),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataCashFlow = data.getJSONArray(tableCashFlow)
                val listCashFlow = mutableListOf<CashFlow>()
                for (i in 0 until dataCashFlow.length()) {
                    val jsonObject = dataCashFlow.getJSONObject(i)
                    listCashFlow.add(
                        CashFlow(
                            id = jsonObject.getString(columnId),
                            note = jsonObject.getString(columnNote),
                            nominal = jsonObject.getString(columnNominal),
                            typePayment = jsonObject.getInt(columnTypePayment),
                            type = jsonObject.getInt(columnType),
                            creditTenantId = jsonObject.getString(columnCreditTenantId),
                            creditDebitId = jsonObject.getString(columnCreditDebitId),
                            unitId = jsonObject.getString(columnUnitId),
                            tenantId = jsonObject.getString(columnTenantId),
                            kostId = jsonObject.getString(columnKostId),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataBooking = data.getJSONArray(tableBooking)
                val listBooking = mutableListOf<Booking>()
                for (i in 0 until dataBooking.length()) {
                    val jsonObject = dataBooking.getJSONObject(i)
                    listBooking.add(
                        Booking(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            numberPhone = jsonObject.getString(columnNumberPhone),
                            note = jsonObject.getString(columnNote),
                            nominal = jsonObject.getString(columnNominal),
                            planCheckIn = jsonObject.getString(columnPlanCheckIn),
                            unitId = jsonObject.getString(columnUnitId),
                            kostId = jsonObject.getString(columnKostId),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataCreditTenant = data.getJSONArray(tableCreditTenant)
                val listCreditTenant = mutableListOf<CreditTenant>()
                for (i in 0 until dataCreditTenant.length()) {
                    val jsonObject = dataCreditTenant.getJSONObject(i)
                    listCreditTenant.add(
                        CreditTenant(
                            id = jsonObject.getString(columnId),
                            note = jsonObject.getString(columnNote),
                            tenantId = jsonObject.getString(columnTenantId),
                            remainingDebt = jsonObject.getInt(columnRemainingDebt),
                            kostId = jsonObject.getString(columnKostId),
                            unitId = jsonObject.getString(columnUnitId),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataCreditDebit = data.getJSONArray(tableCreditDebit)
                val listCreditDebit = mutableListOf<CreditDebit>()
                for (i in 0 until dataCreditDebit.length()) {
                    val jsonObject = dataCreditDebit.getJSONObject(i)
                    listCreditDebit.add(
                        CreditDebit(
                            id = jsonObject.getString(columnId),
                            note = jsonObject.getString(columnNote),
                            status = jsonObject.getInt(columnStatus),
                            remaining = jsonObject.getInt(columnRemaining),
                            customerCreditDebitId = jsonObject.getString(columnCustomerCreditDebitId),
                            dueDate = jsonObject.getString(columnDueDate),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                val dataCustomerCreditDebit = data.getJSONArray(tableCustomerCreditDebit)
                val listCustomerCreditDebit = mutableListOf<CustomerCreditDebit>()
                for (i in 0 until dataCustomerCreditDebit.length()) {
                    val jsonObject = dataCustomerCreditDebit.getJSONObject(i)
                    listCustomerCreditDebit.add(
                        CustomerCreditDebit(
                            id = jsonObject.getString(columnId),
                            name = jsonObject.getString(columnName),
                            numberPhone = jsonObject.getString(columnNumberPhone),
                            note = jsonObject.getString(columnNote),
                            email = jsonObject.getString(columnEmail),
                            createAt = jsonObject.getString(columnCreateAt),
                            isDelete = jsonObject.getBoolean(columnIsDelete)
                        )
                    )
                }

                repository.prosesInsertRestore(
                    listUser,
                    listKost,
                    listUnitStatus,
                    listUnitType,
                    listUnit,
                    listTenant,
                    listCashFlow,
                    listBooking,
                    listCreditTenant,
                    listCreditDebit,
                    listCustomerCreditDebit
                )

                _isProsesValid.value = ValidationResult.Success("Proses Insert Data Restore Sukses")

            } catch (e: Exception) {
                _isProsesValid.value =
                    ValidationResult.Error("Proses Insert Data Restore Gagal " + e.message.toString())
            }

        }
    }

    fun getLastBackUp() {
        viewModelScope.launch {
            try {

                val gson = Gson()
                val response = repository.getLastBackUp(stateUi.value.token)
                Log.d("mylog", response.toString())
                if (response.status) {
                    val data = gson.fromJson(response.data, LastBackupResponse::class.java)
                    if (data.lastBackUp == "2022-01-01") {
                        _stateUi.value = stateUi.value.copy(lastBackUp = "")
                    } else {
                        _stateUi.value = stateUi.value.copy(lastBackUp = data.lastBackUp)
                    }

                    //check token cahce
                    if (data.token != stateUi.value.token) {
                        logOut()
                        _isProsesCheckTokenValid.value =
                            ValidationResult.Error("Dalam Proses Perbaikan... Kode=7073N")
                    }
                } else {
                    if (response.message == "Silahkan Login Kembali") {
                        logOut()
                    }
                    _isProsesCheckTokenValid.value =
                        ValidationResult.Error(response.message)
                }

            } catch (e: Exception) {
                Log.e("mylog", e.toString())
            }
        }
    }

    fun logOut() {
        accountBackupPreference.logOut()
    }

    private fun clearError() {
        _isProsesValid.value = ValidationResult.None
        _isProsesRestoreValid.value = ValidationResult.None
        _isProsesCheckTokenValid.value = ValidationResult.None
    }

}