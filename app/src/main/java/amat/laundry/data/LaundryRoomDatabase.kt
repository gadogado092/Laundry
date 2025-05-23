package amat.laundry.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class, Category::class, LaundryStatus::class,
        Product::class, TransactionLaundry::class, DetailTransaction::class,
        Cart::class, Customer::class, CashFlow::class, CashFlowCategory::class,
        Cashier::class
    ],
    version = 1
)
abstract class LaundryRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun laundryStatusDao(): LaundryStatusDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionLaundryDao
    abstract fun detailTransactionDao(): DetailTransactionDao
    abstract fun customerDao(): CustomerDao
    abstract fun cashierDao(): CashierDao
    abstract fun cashFlowDao(): CashFlowDao
    abstract fun cashFlowCategoryDao(): CashFlowCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: LaundryRoomDatabase? = null

        fun getDatabase(context: Context): LaundryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaundryRoomDatabase::class.java,
                    "laundry_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}