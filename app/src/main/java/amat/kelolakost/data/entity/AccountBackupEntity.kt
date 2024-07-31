package amat.kelolakost.data.entity

data class AccountBackupEntity(
    var isLogin: Boolean = false,
    var token: String = "",
    var name: String = "",
    var noWa: String = ""
)
