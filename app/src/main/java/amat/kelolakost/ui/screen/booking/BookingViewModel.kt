package amat.kelolakost.ui.screen.booking

import amat.kelolakost.data.BookingHome
import amat.kelolakost.data.repository.BookingRepository
import amat.kelolakost.ui.common.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BookingViewModel(private val repository: BookingRepository) : ViewModel() {
    private val _stateListBooking: MutableStateFlow<UiState<List<BookingHome>>> =
        MutableStateFlow(UiState.Loading)
    val stateListBooking: StateFlow<UiState<List<BookingHome>>>
        get() = _stateListBooking

    init {
        getAllBooking()
    }

    fun getAllBooking() {
        viewModelScope.launch {
            _stateListBooking.value = UiState.Loading
            repository.getAllBooking()
                .catch {
                    _stateListBooking.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    _stateListBooking.value = UiState.Success(data)
                }
        }

    }

}

class BookingViewModelFactory(private val repository: BookingRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}