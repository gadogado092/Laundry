package amat.kelolakost.data.repository

import amat.kelolakost.data.Booking
import amat.kelolakost.data.BookingDao
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CashFlowDao
import amat.kelolakost.data.CreditDebit
import amat.kelolakost.data.CreditDebitDao
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CreditTenantDao
import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.data.CustomerCreditDebitDao
import amat.kelolakost.data.Kost
import amat.kelolakost.data.KostDao
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.TenantDao
import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitDao
import amat.kelolakost.data.UnitStatus
import amat.kelolakost.data.UnitStatusDao
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.UnitTypeDao
import amat.kelolakost.data.User
import amat.kelolakost.data.UserDao
import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance
import okhttp3.MultipartBody

class BackUpRepository(
    private val userDao: UserDao,
    private val kosDao: KostDao,
    private val unitStatusDao: UnitStatusDao,
    private val unitTypeDao: UnitTypeDao,
    private val unitDao: UnitDao,
    private val tenantDao: TenantDao,
    private val cashFlowDao: CashFlowDao,
    private val bookingDao: BookingDao,
    private val creditTenantDao: CreditTenantDao,
    private val creditDebitDao: CreditDebitDao,
    private val customerCreditDebitDao: CustomerCreditDebitDao,
) {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun getLastBackUp(token: String): ResponseModel {
        return backUpService.getLastBackUp(token)
    }

    suspend fun backup(token: String, file: MultipartBody.Part): ResponseModel {
        return backUpService.backup(token, file)
    }

    suspend fun getUser(): List<User> {
        return userDao.getUser()
    }

    suspend fun getKostList(): List<Kost> {
        return kosDao.getKostList()
    }

    suspend fun getUnitStatus(): List<UnitStatus> {
        return unitStatusDao.getUnitStatus()
    }

    suspend fun getListUnitType(): List<UnitType> {
        return unitTypeDao.getListUnitType()
    }

    suspend fun getListUnit(): List<Unit> {
        return unitDao.getListUnit()
    }

    suspend fun getListTenant(): List<Tenant> {
        return tenantDao.getListTenant()
    }

    suspend fun getListCashFlow(): List<CashFlow> {
        return cashFlowDao.getListCashFlow()
    }

    suspend fun getListBooking(): List<Booking> {
        return bookingDao.getListBooking()
    }

    suspend fun getListCreditTenant(): List<CreditTenant> {
        return creditTenantDao.getListCreditTenant()
    }

    suspend fun getListCreditDebit(): List<CreditDebit> {
        return creditDebitDao.getListCreditDebit()
    }

    suspend fun getListCustomerCreditDebit(): List<CustomerCreditDebit> {
        return customerCreditDebitDao.getListCustomerCreditDebit()
    }

    suspend fun prosesInsertRestore(
        dataUser: List<User>,
        dataKost: List<Kost>,
        dataUnitStatus: List<UnitStatus>,
        dataUnitType: List<UnitType>,
        dataUnit: List<Unit>,
        dataTenant: List<Tenant>,
        dataCashFlow: List<CashFlow>,
        dataBooking: List<Booking>,
        dataCreditTenant: List<CreditTenant>,
        dataCreditDebit: List<CreditDebit>,
        dataCustomerCreditDebit: List<CustomerCreditDebit>
    ) {
        userDao.prosesInsertRestore(
            dataUser,
            dataKost,
            dataUnitStatus,
            dataUnitType,
            dataUnit,
            dataTenant,
            dataCashFlow,
            dataBooking,
            dataCreditTenant,
            dataCreditDebit,
            dataCustomerCreditDebit
        )
    }

    companion object {
        @Volatile
        private var instance: BackUpRepository? = null

        fun getInstance(
            userDao: UserDao,
            kostDao: KostDao,
            unitStatusDao: UnitStatusDao,
            unitTypeDao: UnitTypeDao,
            unitDao: UnitDao,
            tenantDao: TenantDao,
            cashFlowDao: CashFlowDao,
            bookingDao: BookingDao,
            creditTenantDao: CreditTenantDao,
            creditDebitDao: CreditDebitDao,
            customerCreditDebitDao: CustomerCreditDebitDao
        ): BackUpRepository =
            instance ?: synchronized(this) {
                BackUpRepository(
                    userDao,
                    kostDao,
                    unitStatusDao,
                    unitTypeDao,
                    unitDao,
                    tenantDao,
                    cashFlowDao,
                    bookingDao,
                    creditTenantDao,
                    creditDebitDao,
                    customerCreditDebitDao
                ).apply {
                    instance = this
                }
            }
    }

}