package amat.kelolakost.data.repository

import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance

class RegisterRepository {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun register(
        name: String,
        numberWa: String,
        email: String,
        password: String
    ): ResponseModel {
        return backUpService.register(name, numberWa, email, password)
    }

}