package amat.kelolakost.data.entity

import java.io.Serializable

data class BillEntity(
    val createAt: String = "",
    var nominal: String = "",
    var note: String = "",
    var kostName: String = ""
): Serializable
