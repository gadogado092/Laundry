package amat.laundry.data.response

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    @SerializedName("url_file") val urlFile: String
)