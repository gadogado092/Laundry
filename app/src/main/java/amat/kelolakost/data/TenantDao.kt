package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Query("SELECT * FROM Tenant WHERE isDelete=0 AND id!=0")
    fun getAllTenant(): Flow<List<Tenant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tenant: Tenant)

    @Update
    suspend fun update(tenant: Tenant)
}