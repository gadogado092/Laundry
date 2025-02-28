package amat.laundry.data

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
abstract class LaundryRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

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