package amat.kelolakost.ui.screen.user

import amat.kelolakost.data.Kost
import amat.kelolakost.data.User
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository
) :
    ViewModel() {

    private val _user: MutableStateFlow<User> =
        MutableStateFlow(User("", "", "", "", "", 25000, "", ""))
    val user: StateFlow<User>
        get() = _user

    private val _kost: MutableStateFlow<Kost> =
        MutableStateFlow(Kost("", "", "", "", ""))
    val kost: StateFlow<Kost>
        get() = _kost

    fun setName(value: String) {
        _user.value = _user.value.copy(name = value)

    }

    fun setNumberPhone(value: String) {
        _user.value = _user.value.copy(numberPhone = value)
    }

    fun setEmail(value: String) {
        _user.value = _user.value.copy(email = value)
    }

    fun setKostName(value: String) {
        _kost.value = _kost.value.copy(name = value)
    }

    fun setKostAddress(value: String) {
        _kost.value = _kost.value.copy(address = value)
    }

    fun setNote(value: String) {
        _kost.value = _kost.value.copy(note = value)
    }


    suspend fun insertNewUser(user: User, kost: Kost) {
        userRepository.insertUser(user)
        kostRepository.insertKost(kost)
    }

//    fun isEntryValid(): ValidationResult {
//
//    }

}

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val kostRepository: KostRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository, kostRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}