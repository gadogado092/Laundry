package amat.kelolakost.data.repository

import amat.kelolakost.data.Booking
import amat.kelolakost.data.BookingDao
import amat.kelolakost.data.BookingHome
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenant
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
        bookingDao.prosesCheckIn(
            cashFlow = cashFlow,
            creditTenant = creditTenant,
            isFullPayment = isFullPayment,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = noteAdditionalCost,
            guaranteeCost = guaranteeCost,
            bookingId = bookingId
        )
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