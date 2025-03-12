package amat.laundrysederhana.ui.screen.splash

import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.repository.UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class SplashViewModel(private val repository: UserRepository) : ViewModel() {

    fun getAllUser(): LiveData<List<User>> {
        return repository.getAllUser().asLiveData()
    }

}

class SplashViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}