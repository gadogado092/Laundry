package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: Unit)

    @Update
    suspend fun update(unit: Unit)

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, Unit.noteMaintenance AS noteMaintenance, Unit.unitStatusId AS unitStatusId, " +
                "Kost.name AS kostName, " +
                "Tenant.name AS tenantName, Tenant.limitCheckOut AS limitCheckOut, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear, UnitType.priceGuarantee AS priceGuarantee " +
                "FROM Unit " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.limitCheckOut FROM Tenant) AS Tenant ON Unit.tenantId = Tenant.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0  AND Unit.unitStatusId=:unitStatusId AND Unit.kostId=:kostId " +
                "ORDER BY Unit.name ASC"
    )
    suspend fun getUnitHome(unitStatusId: String, kostId: String): List<UnitHome>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, Unit.noteMaintenance AS noteMaintenance, Unit.unitStatusId AS unitStatusId, " +
                "Kost.name AS kostName, " +
                "Tenant.name AS tenantName, Tenant.limitCheckOut AS limitCheckOut, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear, UnitType.priceGuarantee AS priceGuarantee " +
                "FROM Unit " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.limitCheckOut FROM Tenant) AS Tenant ON Unit.tenantId = Tenant.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0  AND Unit.id!=0 AND Unit.kostId=:kostId " +
                "ORDER BY Unit.name ASC"
    )
    suspend fun getAllUnitHome(kostId: String): List<UnitHome>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, Unit.note AS note, Unit.noteMaintenance AS noteMaintenance, Unit.unitStatusId AS unitStatusId, Unit.kostId AS kostId, Unit.unitTypeId AS unitTypeId, Unit.tenantId AS tenantId, Unit.isDelete AS isDelete, " +
                "Kost.name AS kostName, " +
                "UnitType.name AS unitTypeName " +
                "FROM Unit " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.id=:id"
    )
    fun getDetail(id: String): Flow<UnitDetail>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear, UnitType.priceGuarantee AS priceGuarantee " +
                "FROM Unit " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0 AND Unit.id!=0 AND Unit.unitStatusId=:unitStatusId " +
                "ORDER BY name ASC"
    )
    suspend fun getUnit(unitStatusId: String): List<UnitAdapter>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear, UnitType.priceGuarantee AS priceGuarantee " +
                "FROM Unit " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0 AND Unit.id!=0 AND Unit.kostId=:kostId AND Unit.unitStatusId=:unitStatusId " +
                "ORDER BY name ASC"
    )
    suspend fun getUnitByKost(kostId: String, unitStatusId: String): List<UnitAdapter>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear, UnitType.priceGuarantee AS priceGuarantee " +
                "FROM Unit " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0 AND Unit.id=:unitId " +
                "ORDER BY name ASC"
    )
    suspend fun getPriceUnit(unitId: String): UnitAdapter

}