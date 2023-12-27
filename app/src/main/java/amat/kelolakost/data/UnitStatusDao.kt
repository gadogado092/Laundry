package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UnitStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listStatus: List<UnitStatus>)

}