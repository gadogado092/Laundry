package amat.laundrysederhana.data.response

import com.google.gson.annotations.SerializedName

data class AccountBackupResponse(
    @SerializedName("token") val token: String,
    @SerializedName("name") val name: String,
    @SerializedName("number_wa") val numberWa: String,
    @SerializedName("email") val email: String
)

data class LastBackupResponse(
    @SerializedName("last_backup") val lastBackUp: String,
    @SerializedName("token") val token: String
)

data class OfflineResponse(
    @SerializedName("file") val file: String
)