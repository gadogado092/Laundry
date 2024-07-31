package amat.kelolakost.data.repository

import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance

class LoginRepository {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun login(email: String, password: String): ResponseModel {
        return backUpService.login(email, password)
    }

}