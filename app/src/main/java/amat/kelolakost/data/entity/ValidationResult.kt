package amat.kelolakost.data.entity

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
