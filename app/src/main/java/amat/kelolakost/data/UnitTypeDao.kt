package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UnitTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unitType: UnitType)

}