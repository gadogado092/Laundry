package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditTenantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(creditTenant: CreditTenant)

    @Query("SELECT * FROM CreditTenant")
    fun getAllCreditTenant(): Flow<List<CreditTenant>>

    @Update
    suspend fun update(creditTenant: CreditTenant)

}