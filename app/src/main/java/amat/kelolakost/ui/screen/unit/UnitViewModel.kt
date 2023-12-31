package amat.kelolakost.ui.screen.unit

import amat.kelolakost.data.Kost
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UnitViewModel(private val kostRepository: KostRepository) : ViewModel() {

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

    fun updateStatusSelected(title: String, value: String) {
        _statusSelected.value = FilterEntity(title, value)
        getUnit()
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
                        updateKostSelected(data[0])
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

    fun getUnit() {
        Log.d(
            "saya",
            "get unit, kost = ${kostSelected.value.name}, status = ${statusSelected.value.title}"
        )
    }

    fun updateKostSelected(kost: Kost) {
        _kostSelected.value = kost
        getUnit()
    }

}

class UnitViewModelFactory(private val kostRepository: KostRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitViewModel::class.java)) {
            return UnitViewModel(kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}