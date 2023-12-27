package amat.kelolakost.ui.screen.unit

import amat.kelolakost.data.Kost
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.ui.common.UiState
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
        initStatus()
        getAllKostInit()
    }

    private fun initStatus() {
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
//        getRoom()
    }

    private fun getAllKostInit() {
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
                    updateKostSelected(data[0])
                }
        }

    }

    fun updateKostSelected(kost: Kost) {
        _kostSelected.value = kost
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