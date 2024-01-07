package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(credit: Credit)

    @Query("SELECT * FROM Credit")
    fun getAllCredit(): Flow<List<Credit>>

    @Update
    suspend fun update(credit: Credit)

}