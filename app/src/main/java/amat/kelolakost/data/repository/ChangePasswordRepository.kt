package amat.kelolakost.data.repository

import amat.kelolakost.data.response.ResponseModel
import amat.kelolakost.ui.screen.back_up.RetrofitInstance

class ChangePasswordRepository {
    private val backUpService = RetrofitInstance.backUpService

    suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): ResponseModel {
        return backUpService.changePassword(token, currentPassword, newPassword)
    }

}