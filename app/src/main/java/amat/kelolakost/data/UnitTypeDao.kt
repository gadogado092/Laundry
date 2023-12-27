package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unitType: UnitType)

    @Query("SELECT * FROM UnitType WHERE isDelete=0")
    fun getAllUnitType(): Flow<List<UnitType>>
}