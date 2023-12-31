package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kost: Kost)

    @Query("SELECT * FROM Kost WHERE isDelete=0 AND id!=0 ORDER BY name ASC")
    fun getAllKost(): Flow<List<Kost>>

    @Query("SELECT * FROM Kost WHERE isDelete=0 AND id!=0 ORDER BY name ASC")
    suspend fun getAllKostOrder(): List<Kost>

    @Query("SELECT * FROM Kost WHERE isDelete=0 AND id!=0")
    suspend fun getKost(): List<Kost>

    @Update
    suspend fun update(kost: Kost)

    @Query("SELECT * FROM Kost WHERE id = :id")
    fun getDetail(id: String): Flow<Kost>

}