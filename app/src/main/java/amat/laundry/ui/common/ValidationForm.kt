package amat.laundry.ui.common

data class ValidationForm(
    val isError: Boolean = false,
    val errorMessage: String = ""
)