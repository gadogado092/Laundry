package amat.kelolakost.di

import amat.kelolakost.data.KelolaKostRoomDatabase
import amat.kelolakost.data.repository.UserRepository
import android.content.Context

object Injection {
    fun provideMainRepository(context: Context): UserRepository {
        return UserRepository.getInstance(KelolaKostRoomDatabase.getDatabase(context).userDao())
    }
}