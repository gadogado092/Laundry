package amat.laundrysederhana.data.entity

data class ValidationResult(
    val isError: Boolean,
    val errorMessage: String = ""
)
