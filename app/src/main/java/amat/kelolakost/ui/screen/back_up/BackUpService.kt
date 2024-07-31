package amat.kios.kiossembako.ui.screen.back_up

import amat.kelolakost.data.response.ResponseModel
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface BackUpService {
    @GET("offline/userks/backup_date")
    suspend fun getLastBackUp(
        @Header("Authorization") token: String
    ): ResponseModel

    @FormUrlEncoded
    @POST("offline/userks/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseModel

    @FormUrlEncoded
    @POST("offline/userks/register")
    suspend fun register(
        @Field("name") nama: String,
        @Field("number_wa") numberWa: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseModel

    @FormUrlEncoded
    @POST("offline/userks/change_password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String
    ): ResponseModel

    @FormUrlEncoded
    @POST("offline/userks/code_forget_password")
    suspend fun sendCodeForgetPassword(
        @Field("email") email: String
    ): ResponseModel

    @FormUrlEncoded
    @POST("offline/userks/change_password_forget")
    suspend fun forgetPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
        @Field("code") code: String
    ): ResponseModel

    @Multipart
    @POST("offline/userks/backup")
    suspend fun backup(
        @Header("Authorization") token: String,
        @Part("back_up") file: MultipartBody.Part
    ): ResponseModel
}