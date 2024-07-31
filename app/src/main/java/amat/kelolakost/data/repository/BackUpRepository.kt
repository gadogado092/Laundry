package amat.kelolakost.data.repository

import amat.kelolakost.data.KostDao
import amat.kelolakost.data.UserDao
import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance
import okhttp3.MultipartBody

class BackUpRepository(
    private val userDao: UserDao,
    private val kosDao: KostDao
) {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun getLastBackUp(token: String): ResponseModel {
        return backUpService.getLastBackUp(token)
    }

    suspend fun backup(token: String, file: MultipartBody.Part): ResponseModel {
        return backUpService.backup(token, file)
    }

//    suspend fun getAllProduct(): List<Product> {
//        return productDao.getAllProduct()
//    }
//
//    suspend fun prosesInsertRestore(
//        dataProduct: List<Product>,
//        dataProductUnit: List<ProductUnit>
//    ) {
//        productDao.prosesInsertRestore(dataProduct, dataProductUnit)
//    }
//
//    suspend fun getAllProductUnit(): List<ProductUnit> {
//        return productUnitDao.getAllProductUnit()
//    }

    companion object {
        @Volatile
        private var instance: BackUpRepository? = null

        fun getInstance(
            userDao: UserDao,
            kostDao: KostDao
        ): BackUpRepository =
            instance ?: synchronized(this) {
                BackUpRepository(userDao, kostDao).apply {
                    instance = this
                }
            }
    }

}