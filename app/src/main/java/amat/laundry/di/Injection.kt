package amat.laundry.di

import amat.laundry.data.LaundryRoomDatabase
import amat.laundry.data.repository.CartRepository
import amat.laundry.data.repository.CashFlowCategoryRepository
import amat.laundry.data.repository.CashFlowRepository
import amat.laundry.data.repository.CashierRepository
import amat.laundry.data.repository.CategoryRepository
import amat.laundry.data.repository.CustomerRepository
import amat.laundry.data.repository.DetailTransactionRepository
import amat.laundry.data.repository.ProductRepository
import amat.laundry.data.repository.TransactionRepository
import amat.laundry.data.repository.UserRepository
import android.content.Context

object Injection {
    fun provideMainRepository(context: Context): UserRepository {
        return UserRepository.getInstance(LaundryRoomDatabase.getDatabase(context).userDao())
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(LaundryRoomDatabase.getDatabase(context).userDao())
    }

    fun provideProductRepository(context: Context): ProductRepository {
        return ProductRepository.getInstance(LaundryRoomDatabase.getDatabase(context).productDao())
    }

    fun provideCategoryRepository(context: Context): CategoryRepository {
        return CategoryRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).categoryDao()
        )
    }

    fun provideCartRepository(context: Context): CartRepository {
        return CartRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).cartDao()
        )
    }

    fun provideTransactionRepository(context: Context): TransactionRepository {
        return TransactionRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).transactionDao()
        )
    }

    fun provideDetailTransactionRepository(context: Context): DetailTransactionRepository {
        return DetailTransactionRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).detailTransactionDao()
        )
    }

    fun provideCashFlowCategoryRepository(context: Context): CashFlowCategoryRepository {
        return CashFlowCategoryRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).cashFlowCategoryDao()
        )
    }

    fun provideCashFlowRepository(context: Context): CashFlowRepository {
        return CashFlowRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).cashFlowDao()
        )
    }

    fun provideCustomerRepository(context: Context): CustomerRepository {
        return CustomerRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).customerDao()
        )
    }

    fun provideCashierRepository(context: Context): CashierRepository {
        return CashierRepository.getInstance(
            LaundryRoomDatabase.getDatabase(context).cashierDao()
        )
    }

}