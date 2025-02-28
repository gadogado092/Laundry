package amat.laundry.di

import amat.laundry.data.LaundryRoomDatabase
import amat.laundry.data.repository.UserRepository
import android.content.Context

object Injection {
    fun provideMainRepository(context: Context): UserRepository {
        return UserRepository.getInstance(LaundryRoomDatabase.getDatabase(context).userDao())
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(LaundryRoomDatabase.getDatabase(context).userDao())
    }

//    fun provideKostRepository(context: Context): KostRepository {
//        return KostRepository.getInstance(LaundryRoomDatabase.getDatabase(context).kostDao())
//    }
//
//    fun provideUnitStatusRepository(context: Context): UnitStatusRepository {
//        return UnitStatusRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).unitStatusDao()
//        )
//    }
//
//    fun provideUnitTypeRepository(context: Context): UnitTypeRepository {
//        return UnitTypeRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).unitTypeDao(),
//            LaundryRoomDatabase.getDatabase(context).unitDao()
//        )
//    }
//
//    fun provideUnitRepository(context: Context): UnitRepository {
//        return UnitRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).unitDao()
//        )
//    }
//
//    fun provideTenantRepository(context: Context): TenantRepository {
//        return TenantRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).tenantDao()
//        )
//    }
//
//    fun provideBookingRepository(context: Context): BookingRepository {
//        return BookingRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).bookingDao()
//        )
//    }
//
//    fun provideCashFlowRepository(context: Context): CashFlowRepository {
//        return CashFlowRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).cashFlowDao()
//        )
//    }
//
//    fun provideCreditDebitRepository(context: Context): CreditDebitRepository {
//        return CreditDebitRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).creditDao()
//        )
//    }
//
//    fun provideCreditTenantRepository(context: Context): CreditTenantRepository {
//        return CreditTenantRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).creditTenantDao()
//        )
//    }
//
//    fun provideCustomerCreditDebitRepository(context: Context): CustomerCreditDebitRepository {
//        return CustomerCreditDebitRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).customerCreditDebitDao()
//        )
//    }
//
//    fun provideBackUpRepository(context: Context): BackUpRepository {
//        return BackUpRepository.getInstance(
//            LaundryRoomDatabase.getDatabase(context).userDao(),
//            LaundryRoomDatabase.getDatabase(context).kostDao(),
//            LaundryRoomDatabase.getDatabase(context).unitStatusDao(),
//            LaundryRoomDatabase.getDatabase(context).unitTypeDao(),
//            LaundryRoomDatabase.getDatabase(context).unitDao(),
//            LaundryRoomDatabase.getDatabase(context).tenantDao(),
//            LaundryRoomDatabase.getDatabase(context).cashFlowDao(),
//            LaundryRoomDatabase.getDatabase(context).bookingDao(),
//            LaundryRoomDatabase.getDatabase(context).creditTenantDao(),
//            LaundryRoomDatabase.getDatabase(context).creditDao(),
//            LaundryRoomDatabase.getDatabase(context).customerCreditDebitDao()
//        )
//    }

}