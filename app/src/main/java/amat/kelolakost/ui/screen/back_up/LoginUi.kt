package amat.kelolakost.ui.screen.back_up

data class UserUi(
    val email: String = "",
    val password: String = ""
)

data class BackUpUi(
    val lastBackUp: String = "",
    val token: String = "",
    val name: String = "",
    val noWa: String = ""
)

data class RegisterUi(
    val name: String = "",
    val numberPhone: String = "",
    val email: String = "",
    val repeatEmail: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)

data class ChangePasswordUi(
    val currentPassword: String = "",
    val newPassword: String = "",
    val repeatNewPassword: String = ""
)

data class ForgetPasswordUi(
    val email: String = "",
    val newPassword: String = "",
    val repeatNewPassword: String = "",
    val code: String = ""
)