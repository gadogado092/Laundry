package amat.kelolakost.ui.screen.bill

import amat.kelolakost.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillViewModel(private val repository: UserRepository) : ViewModel() {


    private val _typeWa: MutableStateFlow<String> =
        MutableStateFlow("")
    val typeWa: StateFlow<String>
        get() = _typeWa

    init {
        initTypeWa()
    }

    private fun initTypeWa() {
        viewModelScope.launch {
            val data = repository.getProfile()
            _typeWa.value = data.typeWa
        }

    }

}

class BillViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            return BillViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}