package amat.kelolakost.data.repository

import amat.kelolakost.data.Booking
import amat.kelolakost.data.BookingDao
import amat.kelolakost.data.BookingHome
import amat.kelolakost.data.CashFlow
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {
    fun getAllBooking(): Flow<List<BookingHome>> {
        return bookingDao.getAllBooking()
    }

    suspend fun getBooking(bookingId: String): BookingHome {
        return bookingDao.getBooking(bookingId)
    }

    suspend fun insert(booking: Booking) {
        bookingDao.insert(booking)
    }

    suspend fun update(booking: Booking) {
        bookingDao.update(booking)
    }

    suspend fun addBooking(booking: Booking, cashFlow: CashFlow) {
        bookingDao.addBooking(booking, cashFlow)
    }

    suspend fun cancelBooking(cashFlow: CashFlow, bookingId: String) {
        bookingDao.cancelBooking(cashFlow, bookingId)
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