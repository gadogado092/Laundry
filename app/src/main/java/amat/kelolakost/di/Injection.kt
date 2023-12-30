package amat.kelolakost.di

import amat.kelolakost.data.KelolaKostRoomDatabase
import amat.kelolakost.data.repository.KostRepository
import amat.kelolakost.data.repository.TenantRepository
import amat.kelolakost.data.repository.UnitRepository
import amat.kelolakost.data.repository.UnitStatusRepository
import amat.kelolakost.data.repository.UnitTypeRepository
import amat.kelolakost.data.repository.UserRepository
import android.content.Context

object Injection {
    fun provideMainRepository(context: Context): UserRepository {
        return UserRepository.getInstance(KelolaKostRoomDatabase.getDatabase(context).userDao())
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(KelolaKostRoomDatabase.getDatabase(context).userDao())
    }

    fun provideKostRepository(context: Context): KostRepository {
        return KostRepository.getInstance(KelolaKostRoomDatabase.getDatabase(context).kostDao())
    }

    fun provideUnitStatusRepository(context: Context): UnitStatusRepository {
        return UnitStatusRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).unitStatusDao()
        )
    }

    fun provideUnitTypeRepository(context: Context): UnitTypeRepository {
        return UnitTypeRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).unitTypeDao()
        )
    }

    fun provideUnitRepository(context: Context): UnitRepository {
        return UnitRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).unitDao()
        )
    }

    fun provideTenantRepository(context: Context): TenantRepository {
        return TenantRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).tenantDao()
        )
    }
}