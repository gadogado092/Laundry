package amat.kelolakost.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class
    ],
    version = 1
)
abstract class KelolaKostRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: KelolaKostRoomDatabase? = null

        fun getDatabase(context: Context): KelolaKostRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, KelolaKostRoomDatabase::class.java, "kelola_kost_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}