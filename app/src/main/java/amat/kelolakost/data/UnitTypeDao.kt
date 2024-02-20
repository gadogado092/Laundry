package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unitType: UnitType)

    @Query("SELECT * FROM UnitType WHERE isDelete=0 AND id!=0")
    fun getAllUnitType(): Flow<List<UnitType>>

    @Query("SELECT * FROM UnitType WHERE isDelete=0 AND id!=0 ORDER BY name")
    suspend fun getAllUnitTypeOrder(): List<UnitType>

    @Query("SELECT * FROM UnitType WHERE id = :id")
    fun getDetail(id: String): Flow<UnitType>

    @Update
    suspend fun update(unitType: UnitType)

    @Query("UPDATE UnitType SET isDelete=1 WHERE id=:unitTypeId")
    suspend fun deleteUnitType(unitTypeId: String)
}