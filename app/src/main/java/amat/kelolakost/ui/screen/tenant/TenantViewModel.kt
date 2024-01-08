package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.data.TenantHome
import amat.kelolakost.data.User
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.data.repository.UserRepository
import amat.kelolakost.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TenantViewModel(
    private val repository: TenantRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _stateListTenant: MutableStateFlow<UiState<List<TenantHome>>> =
        MutableStateFlow(UiState.Loading)
    val stateListTenant: StateFlow<UiState<List<TenantHome>>>
        get() = _stateListTenant

    private val _statusSelected = MutableStateFlow(FilterEntity("", ""))
    val statusSelected: StateFlow<FilterEntity>
        get() = _statusSelected

    private val _listStatus = MutableStateFlow<List<FilterEntity>>(mutableListOf())
    val listStatus: StateFlow<List<FilterEntity>>
        get() = _listStatus

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", "", "", "", "", "", 0, "", ""))
    val user: StateFlow<User>
        get() = _user

    init {
        getUser()
        initStatus()
        getAllTenant()
    }

    private fun initStatus() {
        Log.d("saya", "initstatus")
        val listStatus = listOf(
            FilterEntity("Semua", ""),
            FilterEntity("Penghuni", "1"),
            FilterEntity("Keluar", "0")
        )
        _listStatus.value = listStatus
        _statusSelected.value = listStatus[1]
    }

    fun getAllTenant() {
        Log.d("saya", "init tenant")
        viewModelScope.launch {
            _stateListTenant.value = UiState.Loading
            try {
                val data = repository.getAllTenantHome(_statusSelected.value.value)
                _stateListTenant.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListTenant.value = UiState.Error("Error ${e.message.toString()}")
            }
        }

    }

    fun updateStatusSelected(title: String, value: String) {
        _statusSelected.value = FilterEntity(title, value)
        getAllTenant()
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

}

class TenantViewModelFactory(
    private val repository: TenantRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TenantViewModel::class.java)) {
            return TenantViewModel(repository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}