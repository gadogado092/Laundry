package amat.kelolakost.data.response

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: JsonObject = JsonObject()
)