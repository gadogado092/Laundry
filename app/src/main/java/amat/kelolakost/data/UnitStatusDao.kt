package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnitStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listStatus: List<UnitStatus>)

    @Query("SELECT * FROM UnitStatus")
    suspend fun getUnitStatus(): List<UnitStatus>

}