package amat.kelolakost.data.repository

import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance

class ForgetPasswordRepository {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun forgetPassword(
        email: String,
        newPassword: String,
        code: String
    ): ResponseModel {
        return backUpService.forgetPassword(email, newPassword, code)
    }

    suspend fun sendCodeForgetPassword(
        email: String
    ): ResponseModel {
        return backUpService.sendCodeForgetPassword(email)
    }

}