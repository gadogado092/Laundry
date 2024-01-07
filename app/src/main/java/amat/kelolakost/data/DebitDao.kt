package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debit: Debit)

    @Query("SELECT * FROM Debit")
    fun getAllDebit(): Flow<List<Debit>>

    @Update
    suspend fun update(debit: Debit)

}