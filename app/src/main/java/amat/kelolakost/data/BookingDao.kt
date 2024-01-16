package amat.kelolakost.data

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

    @Query("SELECT * FROM Booking")
    fun getAllBooking(): Flow<List<Booking>>

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

}