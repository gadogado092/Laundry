package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear " +
                "FROM Unit " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.limitCheckOut FROM Tenant) AS Tenant ON Unit.tenantId = Tenant.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0  AND Unit.unitStatusId=:unitStatusId AND Unit.kostId=:kostId " +
                "ORDER BY Unit.name ASC"
    )
    suspend fun getUnitHome(unitStatusId: String, kostId: String): List<UnitHome>

    @Query(
        "SELECT Unit.id AS id, Unit.name AS name, Unit.noteMaintenance AS noteMaintenance, Unit.unitStatusId AS unitStatusId, " +
                "Kost.name AS kostName, " +
                "Tenant.name AS tenantName, Tenant.limitCheckOut AS limitCheckOut, " +
                "UnitType.name AS unitTypeName, UnitType.priceDay AS priceDay, UnitType.priceWeek AS priceWeek, UnitType.priceMonth AS priceMonth, UnitType.priceThreeMonth AS priceThreeMonth, UnitType.priceSixMonth AS priceSixMonth, UnitType.priceYear AS priceYear " +
                "FROM Unit " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.limitCheckOut FROM Tenant) AS Tenant ON Unit.tenantId = Tenant.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceDay, UnitType.priceWeek, UnitType.priceMonth, UnitType.priceThreeMonth, UnitType.priceSixMonth, UnitType.priceYear FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "WHERE Unit.isDelete=0  AND Unit.id!=0 AND Unit.kostId=:kostId " +
                "ORDER BY Unit.name ASC"
    )
    suspend fun getAllUnitHome(kostId: String): List<UnitHome>

}