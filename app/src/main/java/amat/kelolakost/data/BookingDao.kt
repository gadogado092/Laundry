package amat.kelolakost.data

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Query(
        "SELECT Booking.id AS id, Booking.name AS name, Booking.numberPhone AS numberPhone, Booking.planCheckIn AS planCheckIn, Booking.nominal AS nominal, Booking.note AS note, " +
                "Unit.id AS unitId, Unit.name AS unitName, UnitType.name AS unitTypeName, Kost.id AS kostId, kost.name AS kostName, UnitType.priceGuarantee AS priceGuarantee, Unit.unitStatusId AS unitStatusId " +
                "FROM Booking " +
                "LEFT JOIN (SELECT Unit.id, Unit.unitTypeId, Unit.kostId, Unit.name, Unit.unitStatusId FROM Unit) AS Unit ON Booking.unitId = Unit.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Booking.isDelete=0 AND Booking.id!=0 ORDER BY Booking.planCheckIn DESC"
    )
    fun getAllBooking(): Flow<List<BookingHome>>

    @Query(
        "SELECT Booking.id AS id, Booking.name AS name, Booking.numberPhone AS numberPhone, Booking.planCheckIn AS planCheckIn, Booking.nominal AS nominal, Booking.note AS note, " +
                "Unit.id AS unitId, Unit.name AS unitName, UnitType.name AS unitTypeName, Kost.id AS kostId, kost.name AS kostName, UnitType.priceGuarantee AS priceGuarantee, Unit.unitStatusId AS unitStatusId " +
                "FROM Booking " +
                "LEFT JOIN (SELECT Unit.id, Unit.unitTypeId, Unit.kostId, Unit.name, Unit.unitStatusId FROM Unit) AS Unit ON Booking.unitId = Unit.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name, UnitType.priceGuarantee FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Booking.isDelete=0 AND Booking.id=:bookingId ORDER BY Booking.planCheckIn DESC"
    )
    suspend fun getBooking(bookingId: String): BookingHome

    @Update
    suspend fun update(booking: Booking)

    //ADD Booking
    @Query("UPDATE Unit SET bookingId=:bookingId WHERE id=:unitId")
    suspend fun updateUnitBookingId(unitId: String, bookingId: String)

    @Transaction
    suspend fun addBooking(booking: Booking, cashFlow: CashFlow) {
        //update unit
        updateUnitBookingId(unitId = booking.unitId, bookingId = booking.id)
        //insert cashflow
        insert(cashFlow)
        //insert booking
        insert(booking)
    }

    //CANCEL Booking
    @Query("UPDATE Booking SET isDelete=1 WHERE id=:bookingId")
    suspend fun deleteBooking(bookingId: String)

    @Transaction
    suspend fun cancelBooking(cashFlow: CashFlow, bookingId: String) {
        //update unit
        updateUnitBookingId(unitId = cashFlow.unitId, bookingId = "0")
        //delete data booking
        deleteBooking(bookingId)
        Log.d("saya cancel", "ada ${cashFlow.nominal}")

        //insert cash flow return booking
        if (cashFlow.nominal != "0") {
            insert(cashFlow)
        }
    }

    //CHECK IN AREA
    @Query("UPDATE Unit SET unitStatusId=:unitStatusId, noteMaintenance=:noteMaintenance, tenantId=:tenantId WHERE id=:unitId")
    suspend fun updateUnit(
        unitId: String,
        tenantId: String,
        unitStatusId: Int,
        noteMaintenance: String
    )

    @Query(
        "UPDATE Tenant " +
                "SET limitCheckOut=:limitCheckOut, " +
                "additionalCost=:additionalCost, " +
                "noteAdditionalCost=:noteAdditionalCost, " +
                "guaranteeCost=:guaranteeCost, " +
                "unitId=:unitId " +
                "WHERE id=:tenantId"
    )
    suspend fun updateTenant(
        tenantId: String, limitCheckOut: String,
        additionalCost: Int, noteAdditionalCost: String, guaranteeCost: Int, unitId: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditTenant(creditTenant: CreditTenant)

    @Transaction
    suspend fun prosesCheckIn(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String,
        guaranteeCost: Int,
        bookingId: String
    ) {
        //eksekusi pakai transaction
        //update tenant
        updateTenant(
            tenantId = cashFlow.tenantId,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = if (additionalCost == 0) "" else noteAdditionalCost,
            guaranteeCost = guaranteeCost,
            unitId = cashFlow.unitId
        )
        //update unit
        updateUnit(
            unitId = cashFlow.unitId,
            tenantId = cashFlow.tenantId,
            unitStatusId = 1,
            noteMaintenance = ""
        )
        //insert credit tenant if not full payment
        if (!isFullPayment) {
            insertCreditTenant(creditTenant)
        }
        //insert cashflow
        insert(cashFlow)

        //update unit
        updateUnitBookingId(unitId = cashFlow.unitId, bookingId = "0")
        //delete data booking
        deleteBooking(bookingId)
    }

    @Query(
        "SELECT * " +
                "FROM Booking " +
                "WHERE Booking.isDelete=0 AND Booking.id!=0 AND Booking.unitId=:unitId " +
                "ORDER BY name ASC"
    )
    //for delete unit
    suspend fun getBookingByUnit(unitId: String): List<Booking>

}