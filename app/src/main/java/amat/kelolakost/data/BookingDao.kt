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
        "SELECT Booking.id AS id, Booking.name AS name, Booking.numberPhone AS numberPhone, Booking.planCheckIn AS planCheckIn, Booking.nominal AS nominal, " +
                "Unit.id AS unitId, Unit.name AS unitName, UnitType.name AS unitTypeName, Kost.id AS kostId, kost.name AS kostName " +
                "FROM Booking " +
                "LEFT JOIN (SELECT Unit.id, Unit.unitTypeId, Unit.kostId, Unit.name FROM Unit) AS Unit ON Booking.unitId = Unit.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
                "LEFT JOIN (SELECT Kost.id, Kost.name FROM Kost) AS Kost ON Unit.kostId = Kost.id " +
                "WHERE Booking.isDelete=0 AND Booking.id!=0 ORDER BY Booking.planCheckIn DESC"
    )
    fun getAllBooking(): Flow<List<BookingHome>>

    @Query(
        "SELECT Booking.id AS id, Booking.name AS name, Booking.numberPhone AS numberPhone, Booking.planCheckIn AS planCheckIn, Booking.nominal AS nominal, " +
                "Unit.id AS unitId, Unit.name AS unitName, UnitType.name AS unitTypeName, Kost.id AS kostId, kost.name AS kostName " +
                "FROM Booking " +
                "LEFT JOIN (SELECT Unit.id, Unit.unitTypeId, Unit.kostId, Unit.name FROM Unit) AS Unit ON Booking.unitId = Unit.id " +
                "LEFT JOIN (SELECT UnitType.id, UnitType.name FROM UnitType) AS UnitType ON Unit.unitTypeId = UnitType.id " +
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
        Log.d("saya cancel","ada ${cashFlow.nominal}")

        //insert cash flow return booking
        if (cashFlow.nominal != "0") {
            insert(cashFlow)
        }
    }

}