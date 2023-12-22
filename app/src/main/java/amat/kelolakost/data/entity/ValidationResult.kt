package amat.kelolakost.data.entity

data class ValidationResult(
    val isError: Boolean,
    val errorMessage: String = ""
)
