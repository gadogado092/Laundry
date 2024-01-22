package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * FROM User")
    suspend fun getUser(): List<User>

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM User LIMIT 1")
    fun getDetail(): Flow<User>

    @Query("UPDATE User SET `limit`=:newLimit, `key`=:newKey  WHERE id=:userId")
    suspend fun extendApp(userId: String, newLimit: String, newKey: String)
}