package amat.kelolakost.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class, Kost::class, UnitStatus::class, UnitType::class, Unit::class, Tenant::class, CashFlow::class,
        Booking::class, Credit::class, CreditTenant::class, CustomerCreditDebit::class, Debit::class
    ],
    version = 1
)
abstract class KelolaKostRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun kostDao(): KostDao
    abstract fun unitStatusDao(): UnitStatusDao
    abstract fun unitTypeDao(): UnitTypeDao
    abstract fun unitDao(): UnitDao
    abstract fun tenantDao(): TenantDao
    abstract fun bookingDao(): BookingDao
    abstract fun cashFlowDao(): CashFlowDao
    abstract fun creditDao(): CreditDao
    abstract fun creditTenantDao(): CreditTenantDao
    abstract fun customerCreditDebitDao(): CustomerCreditDebitDao
    abstract fun debitDao(): DebitDao

    companion object {
        @Volatile
        private var INSTANCE: KelolaKostRoomDatabase? = null

        fun getDatabase(context: Context): KelolaKostRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KelolaKostRoomDatabase::class.java,
                    "kelola_kost_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}