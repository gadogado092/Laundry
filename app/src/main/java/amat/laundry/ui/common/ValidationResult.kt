package amat.laundry.ui.common

sealed class ValidationResult<out T : Any?> {
    object None : ValidationResult<Nothing>()

    data class Success<out T : Any>(val data: T) : ValidationResult<T>()

    data class Error(val errorMessage: String) : ValidationResult<Nothing>()
    data class Loading(val loadingMessage: String) : ValidationResult<Nothing>()
}