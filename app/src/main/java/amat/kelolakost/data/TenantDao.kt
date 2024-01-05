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

    @Query("SELECT * FROM Tenant WHERE isDelete=0 AND id!=0 AND unitId=0")
    suspend fun getTenantCheckOut(): List<Tenant>

    @Query(
        "SELECT Tenant.id AS id, Tenant.name AS name, Tenant.numberPhone AS numberPhone, Tenant.limitCheckOut AS limitCheckOut, " +
                "Unit.id AS unitId, Unit.name AS unitName, " +
                "Kost.name AS kostName " +
                "FROM Tenant " +
                "LEFT JOIN (SELECT Unit.id, Unit.name, Unit.kostId FROM Unit) AS Unit ON Tenant.unitId = Unit.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Tenant.isDelete=0 AND Tenant.id!=0 AND Tenant.unitId!=0 ORDER BY Tenant.name "
    )
    suspend fun getAllTenantHomeCheckIn(): List<TenantHome>

    @Query(
        "SELECT Tenant.id AS id, Tenant.name AS name, Tenant.numberPhone AS numberPhone, Tenant.limitCheckOut AS limitCheckOut, " +
                "Unit.id AS unitId, Unit.name AS unitName, " +
                "Kost.name AS kostName " +
                "FROM Tenant " +
                "LEFT JOIN (SELECT Unit.id, Unit.name, Unit.kostId FROM Unit) AS Unit ON Tenant.unitId = Unit.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Tenant.isDelete=0 AND Tenant.id!=0 AND Tenant.unitId=0 ORDER BY Tenant.name "
    )
    suspend fun getAllTenantHomeCheckOut(): List<TenantHome>

    @Query(
        "SELECT Tenant.id AS id, Tenant.name AS name, Tenant.numberPhone AS numberPhone, Tenant.limitCheckOut AS limitCheckOut, " +
                "Unit.id AS unitId, Unit.name AS unitName, " +
                "Kost.name AS kostName " +
                "FROM Tenant " +
                "LEFT JOIN (SELECT Unit.id, Unit.name, Unit.kostId FROM Unit) AS Unit ON Tenant.unitId = Unit.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Tenant.isDelete=0 AND Tenant.id!=0 ORDER BY Tenant.name "
    )
    suspend fun getAllTenantHome(): List<TenantHome>

    @Query("SELECT * FROM Tenant WHERE id = :id")
    fun getDetail(id: String): Flow<Tenant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tenant: Tenant)

    @Update
    suspend fun update(tenant: Tenant)
}