package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.data.repository.BackUpRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BackUpViewModelFactory(
    private val repository: BackUpRepository,
    private val accountBackupPreference: AccountBackupPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackUpViewModel::class.java)) {
            return BackUpViewModel(repository, accountBackupPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}