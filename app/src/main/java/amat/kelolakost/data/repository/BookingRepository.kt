package amat.kelolakost.data.repository

import amat.kelolakost.data.Booking
import amat.kelolakost.data.BookingDao
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {
    fun getAllUser(): Flow<List<Booking>> {
        return bookingDao.getAllBooking()
    }

    suspend fun insert(booking: Booking) {
        bookingDao.insert(booking)
    }

    suspend fun update(booking: Booking) {
        bookingDao.update(booking)
    }

    companion object {
        @Volatile
        private var instance: BookingRepository? = null

        fun getInstance(dao: BookingDao): BookingRepository =
            instance ?: synchronized(this) {
                BookingRepository(dao).apply {
                    instance = this
                }
            }
    }
}