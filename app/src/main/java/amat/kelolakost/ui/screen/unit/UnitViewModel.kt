package amat.kelolakost.ui.screen.unit

import amat.kelolakost.data.Kost
import amat.kelolakost.data.UnitHome
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UnitViewModel(
    private val kostRepository: KostRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _listStatus = MutableStateFlow<List<FilterEntity>>(mutableListOf())
    val listStatus: StateFlow<List<FilterEntity>>
        get() = _listStatus

    private val _statusSelected = MutableStateFlow(FilterEntity("", ""))
    val statusSelected: StateFlow<FilterEntity>
        get() = _statusSelected

    private val _stateListKost: MutableStateFlow<UiState<List<Kost>>> =
        MutableStateFlow(UiState.Loading)
    val stateListKost: StateFlow<UiState<List<Kost>>>
        get() = _stateListKost

    private val _kostSelected = MutableStateFlow(Kost("", "", "", "", "", false))
    val kostSelected: StateFlow<Kost>
        get() = _kostSelected

    private val _stateListUnit: MutableStateFlow<UiState<List<UnitHome>>> =
        MutableStateFlow(UiState.Loading)
    val stateListUnit: StateFlow<UiState<List<UnitHome>>>
        get() = _stateListUnit

    init {
        Log.d("saya", "init")
        initStatus()
        getAllKost()
    }

    private fun initStatus() {
        Log.d("saya", "initstatus")
        val listStatus = listOf(
            FilterEntity("Semua", "0"),
            FilterEntity("Terisi", "1"),
            FilterEntity("Jatuh Tempo", "5"),
            FilterEntity("Kosong", "2"),
            FilterEntity("Pembersihan", "3"),
            FilterEntity("Perbaikan", "4"),
        )
        _listStatus.value = listStatus
        _statusSelected.value = listStatus[1]
    }

    fun getAllKost() {
        Log.d("saya", "get all kost")
        viewModelScope.launch {
            _stateListKost.value = UiState.Loading
//            val data = kostRepository.getKost()
//            _stateListKost.value = UiState.Success(data)
            kostRepository.getAllKost()
                .catch {
                    _stateListKost.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListKost.value = UiState.Success(data)

                    if (_kostSelected.value.id == "") {
                        _kostSelected.value = data[0]
                    }

                }
        }

//        viewModelScope.launch {
//            _stateListKost.value = UiState.Loading
//            try {
//                val data = kostRepository.getAllKostOrder()
//                _stateListKost.value = UiState.Success(data)
//                updateKostSelected(data[0])
//            } catch (e: Exception) {
//                _stateListKost.value = UiState.Error(e.message.toString())
//            }
//        }

    }

    fun updateKostSelected(kost: Kost) {
        _kostSelected.value = kost
        getUnit()
    }

    fun updateStatusSelected(title: String, value: String) {
        _statusSelected.value = FilterEntity(title, value)
        getUnit()
    }

    fun getUnit() {
        _stateListUnit.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = unitRepository.getUnitHome(
                    unitStatusId = _statusSelected.value.value,
                    kostId = _kostSelected.value.id
                )
                _stateListUnit.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListUnit.value = UiState.Error("Error ${e.message.toString()}")
            }
        }
    }

}

class UnitViewModelFactory(
    private val kostRepository: KostRepository,
    private val unitRepository: UnitRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitViewModel::class.java)) {
            return UnitViewModel(kostRepository, unitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}