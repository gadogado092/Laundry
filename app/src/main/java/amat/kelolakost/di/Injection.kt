package amat.kelolakost.di

import amat.kelolakost.data.KelolaKostRoomDatabase
import amat.kelolakost.data.repository.BackUpRepository
import amat.kelolakost.data.repository.BookingRepository
import amat.kelolakost.data.repository.CashFlowRepository
import amat.kelolakost.data.repository.CreditDebitRepository
import amat.kelolakost.data.repository.CreditTenantRepository
import amat.kelolakost.data.repository.CustomerCreditDebitRepository
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
            KelolaKostRoomDatabase.getDatabase(context).unitTypeDao(),
            KelolaKostRoomDatabase.getDatabase(context).unitDao()
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

    fun provideBookingRepository(context: Context): BookingRepository {
        return BookingRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).bookingDao()
        )
    }

    fun provideCashFlowRepository(context: Context): CashFlowRepository {
        return CashFlowRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).cashFlowDao()
        )
    }

    fun provideCreditDebitRepository(context: Context): CreditDebitRepository {
        return CreditDebitRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).creditDao()
        )
    }

    fun provideCreditTenantRepository(context: Context): CreditTenantRepository {
        return CreditTenantRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).creditTenantDao()
        )
    }

    fun provideCustomerCreditDebitRepository(context: Context): CustomerCreditDebitRepository {
        return CustomerCreditDebitRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).customerCreditDebitDao()
        )
    }

    fun provideBackUpRepository(context: Context): BackUpRepository {
        return BackUpRepository.getInstance(
            KelolaKostRoomDatabase.getDatabase(context).userDao(),
            KelolaKostRoomDatabase.getDatabase(context).kostDao(),
            KelolaKostRoomDatabase.getDatabase(context).unitStatusDao(),
            KelolaKostRoomDatabase.getDatabase(context).unitTypeDao(),
            KelolaKostRoomDatabase.getDatabase(context).unitDao(),
            KelolaKostRoomDatabase.getDatabase(context).tenantDao(),
            KelolaKostRoomDatabase.getDatabase(context).cashFlowDao(),
            KelolaKostRoomDatabase.getDatabase(context).bookingDao(),
            KelolaKostRoomDatabase.getDatabase(context).creditTenantDao(),
            KelolaKostRoomDatabase.getDatabase(context).creditDao(),
            KelolaKostRoomDatabase.getDatabase(context).customerCreditDebitDao()
        )
    }

}