package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
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
import org.json.JSONObject

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

    private val _stateUi: MutableStateFlow<BackUpUi> =
        MutableStateFlow(BackUpUi())
    val stateUi: StateFlow<BackUpUi>
        get() = _stateUi

    val typeUser = "jkn"
    val tableProduct = "tableProduct"
    private val idProduct = "idProduct"
    private val codeProduct = "codeProduct"
    private val nameProduct = "nameProduct"
    private val imageUrl = "imageUrl"
    private val sellPrice = "sellPrice"
    private val buyPrice = "buyPrice"
    private val stock = "stock"
    private val stockWarning = "stockWarning"
    private val idUnit = "idUnit"
    private val idCategory = "idCategory"
    private val isDelete = "isDelete"

    val tableProductUnit = "tableProductUnit"
    private val idProductUnit = "idProductUnit"
    private val nameProductUnit = "nameProductUnit"

    init {
        val data = accountBackupPreference.getAccount()
        _stateUi.value = stateUi.value.copy(name = data.name, token = data.token, noWa = data.noWa)
    }

    fun backUp(context: Context) {
        clearError()
        _isProsesValid.value = ValidationResult.Loading("Loading Proses Backup")

        viewModelScope.launch {
            try {
                //TODO get database
//                val dataProduct = repository.getAllProduct()
//                val dataProductJSON = JSONArray()
//                dataProduct.forEach {
//                    val data = JSONObject()
//                    data.put(idProduct, it.id)
//                    data.put(codeProduct, it.code)
//                    data.put(nameProduct, it.name)
//                    data.put(imageUrl, it.imageUrl)
//                    data.put(sellPrice, it.sellPrice)
//                    data.put(buyPrice, it.buyPrice)
//                    data.put(stock, it.stock)
//                    data.put(stockWarning, it.stockWarning)
//                    data.put(idUnit, it.idUnit)
//                    data.put(idCategory, it.idCategory)
//                    data.put(isDelete, it.isDelete)
//                    dataProductJSON.put(data)
//                }
//
//                val dataProductUnit = repository.getAllProductUnit()
//                val dataProductUnitJSON = JSONArray()
//                dataProductUnit.forEach {
//                    val data = JSONObject()
//                    data.put(idProductUnit, it.id)
//                    data.put(nameProductUnit, it.name)
//                    data.put(isDelete, it.isDelete)
//                    dataProductUnitJSON.put(data)
//                }
//
//                //Collect Data
//                val dataCollect = JSONObject()
//                dataCollect.put(tableProduct, dataProductJSON)
//                dataCollect.put(tableProductUnit, dataProductUnitJSON)
//
//                //Create File
//                val outputDir: File = context.cacheDir
//                val outputFile = File.createTempFile("backup", ".txt", outputDir)
//                val fileWriter = FileWriter(outputFile)
//                val bufferedWriter = BufferedWriter(fileWriter)
//                bufferedWriter.write(dataCollect.toString())
//                bufferedWriter.close()
//
//                Log.d("myloggg", "path file " + outputFile.path)
//
//                val params = RequestParams()
//                params.put("back_up", outputFile)
//                params.put("type_user", typeUser)
//
//                val client = AsyncHttpClient()
//                val baseUrl = "http://apix.juragankost.id/api/"
//                client.addHeader("Authorization", stateUi.value.token)
//                client.setTimeout(20000)
//                client.post(
//                    baseUrl + "offline/userks/backup",
//                    params,
//                    object : AsyncHttpResponseHandler() {
//                        override fun onSuccess(
//                            statusCode: Int,
//                            headers: Array<out Header>,
//                            responseBody: ByteArray
//                        ) {
//                            val result = String(responseBody)
//                            val gson = Gson()
//                            val response = gson.fromJson(result, ResponseModel::class.java)
//
//                            if (response.status) {
//                                _isProsesValid.value = ValidationResult.Success(response.message)
//                            } else {
//                                if (response.message == "Silahkan Login Kembali") {
//                                    accountBackupPreference.logOut()
//                                }
//                                _isProsesValid.value =
//                                    ValidationResult.Error(response.message)
//                            }
//                        }
//
//                        override fun onFailure(
//                            statusCode: Int,
//                            headers: Array<out Header>?,
//                            responseBody: ByteArray?,
//                            error: Throwable?
//                        ) {
//                            _isProsesValid.value =
//                                ValidationResult.Error("Proses Backup Gagal ${error.toString()}")
//                        }
//                    })

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
                                //TODO insert to database
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

//                val dataProduct = data.getJSONArray(tableProduct)
//                val listProduct = mutableListOf<Product>()
//                for (i in 0 until dataProduct.length()) {
//                    val jsonObject = dataProduct.getJSONObject(i)
//                    listProduct.add(
//                        Product(
//                            id = jsonObject.getInt(idProduct),
//                            code = jsonObject.getString(codeProduct),
//                            name = jsonObject.getString(nameProduct),
//                            imageUrl = jsonObject.getString(imageUrl),
//                            sellPrice = jsonObject.getInt(sellPrice),
//                            buyPrice = jsonObject.getInt(buyPrice),
//                            stock = jsonObject.getInt(stock),
//                            stockWarning = jsonObject.getInt(stockWarning),
//                            idUnit = jsonObject.getInt(idUnit),
//                            idCategory = jsonObject.getInt(idCategory),
//                            isDelete = jsonObject.getInt(isDelete)
//                        )
//                    )
//                }
//
//                val dataProductUnit = data.getJSONArray(tableProductUnit)
//                val listProductUnit = mutableListOf<ProductUnit>()
//                for (i in 0 until dataProductUnit.length()) {
//                    val jsonObject = dataProductUnit.getJSONObject(i)
//                    listProductUnit.add(
//                        ProductUnit(
//                            id = jsonObject.getInt(idProductUnit),
//                            name = jsonObject.getString(nameProductUnit),
//                            isDelete = jsonObject.getInt(isDelete)
//                        )
//                    )
//                }
//
//                repository.prosesInsertRestore(listProduct, listProductUnit)
//
//                _isProsesValid.value = ValidationResult.Success("Proses Insert Data Restore Sukses")

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
    }

}