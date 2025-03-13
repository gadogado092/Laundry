package amat.laundrysederhana.ui.screen.product

import amat.laundrysederhana.data.Category
import amat.laundrysederhana.data.Product
import amat.laundrysederhana.data.ProductCategory
import amat.laundrysederhana.data.entity.ValidationResult
import amat.laundrysederhana.data.repository.CategoryRepository
import amat.laundrysederhana.data.repository.ProductRepository
import amat.laundrysederhana.ui.common.UiState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _stateProduct: MutableStateFlow<UiState<ProductCategory>> =
        MutableStateFlow(UiState.Loading)
    val stateProduct: StateFlow<UiState<ProductCategory>>
        get() = _stateProduct

    private val _stateUi: MutableStateFlow<AddProductUi> =
        MutableStateFlow(AddProductUi(categoryName = "Pilih Kategori"))
    val stateUi: StateFlow<AddProductUi>
        get() = _stateUi

    private val _isCategorySelectedValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isCategorySelectedValid: StateFlow<ValidationResult>
        get() = _isCategorySelectedValid

    private val _isProductNameValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isProductNameValid: StateFlow<ValidationResult>
        get() = _isProductNameValid

    private val _isProductPriceValid: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(false, ""))

    val isProductPriceValid: StateFlow<ValidationResult>
        get() = _isProductPriceValid

    private val _stateListCategory: MutableStateFlow<UiState<List<Category>>> =
        MutableStateFlow(UiState.Error(""))
    val stateListCategory: StateFlow<UiState<List<Category>>>
        get() = _stateListCategory

    private val _isProsesFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesFailed: StateFlow<ValidationResult>
        get() = _isProsesFailed

    private val _isProsesDeleteFailed: MutableStateFlow<ValidationResult> =
        MutableStateFlow(ValidationResult(true, ""))

    val isProsesDeleteFailed: StateFlow<ValidationResult>
        get() = _isProsesDeleteFailed

    fun getProduct(id: String) {
        if (id.isNotEmpty()) {
            clearError()
            viewModelScope.launch {
                try {
                    _stateProduct.value = UiState.Loading
                    val data = productRepository.getProductCategoryDetail(id)
                    _stateUi.value = AddProductUi(
                        id = data.productId,
                        name = data.productName,
                        price = data.productPrice.toString(),
                        categoryId = data.categoryId,
                        categoryName = data.categoryName
                    )
                    _stateProduct.value = UiState.Success(data)
                } catch (e: Exception) {
                    _stateProduct.value = UiState.Error(e.message.toString())
                }
            }
        } else {
            val productCategory = ProductCategory("", "", 0, "", "", "")
            _stateProduct.value = UiState.Success(productCategory)
        }
    }

    fun setProductName(value: String) {
        clearError()
        _stateUi.value = _stateUi.value.copy(name = value)
        if (stateUi.value.name.trim().isEmpty()) {
            _isProductNameValid.value = ValidationResult(true, "Nama Produk Tidak Boleh Kosong")
        } else {
            _isProductNameValid.value = ValidationResult(false, "")
        }
    }

    fun setProductPrice(value: String) {

        clearError()

        val cleanValue = value.trim().replace(" ", "")
        if (cleanValue.toIntOrNull() != null) {
            _stateUi.value = stateUi.value.copy(price = cleanValue)
            if (cleanValue.isEmpty() || cleanValue.toInt() < 1) {
                _isProductPriceValid.value =
                    ValidationResult(true, "Harga Produk Tidak Boleh Kosong")
            } else {
                _isProductPriceValid.value = ValidationResult(false, "")
            }
        } else {
            if (cleanValue.isEmpty()) {
                _isProductPriceValid.value =
                    ValidationResult(true, "Harga Produk Tidak Boleh Kosong")
                _stateUi.value = stateUi.value.copy(price = "")
            } else {
                _isProductPriceValid.value =
                    ValidationResult(true, "Masukkan Format Angka Yang Sesuai")
                _stateUi.value = stateUi.value.copy(price = "")
            }

        }

    }

    fun setCategorySelected(id: String, name: String) {
        clearError()
        _stateUi.value = stateUi.value.copy(categoryId = id, categoryName = name)
    }

    fun getCategory() {
        clearError()
        _stateListCategory.value = UiState.Loading
        viewModelScope.launch {
            try {
                val data = categoryRepository.getCategory()
                _stateListCategory.value = UiState.Success(data)
            } catch (e: Exception) {
                _stateListCategory.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun dataIsComplete(): Boolean {
        clearError()
        if (stateUi.value.name.trim().isEmpty()) {
            _isProductNameValid.value = ValidationResult(true, "Nama Produk Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Nama Produk Tidak Boleh Kosong")
            return false
        }

        if (stateUi.value.price.trim().isEmpty() || stateUi.value.price.trim() == "0") {
            _isProductPriceValid.value = ValidationResult(true, "Harga Produk Tidak Boleh Kosong")
            _isProsesFailed.value = ValidationResult(true, "Harga Produk Tidak Boleh Kosong")
            return false
        }

        if (stateUi.value.categoryId == "") {
            _isCategorySelectedValid.value = ValidationResult(true, "Silahkan Pilih Kategori")
            _isProsesFailed.value = ValidationResult(true, "Silahkan Pilih Kategori")
            return false
        }

        return true
    }

    fun dataDeleteIsComplete(): Boolean {
        if (stateUi.value.id.isEmpty()) {
            _isProsesDeleteFailed.value = ValidationResult(true, "Id Data Tidak Ada")
            return false
        }
        return true
    }

    fun process() {
        try {
            viewModelScope.launch {
                val dataProduct = stateUi.value
                if (stateUi.value.id.isNotEmpty()) {
                    productRepository.update(
                        Product(
                            id = dataProduct.id,
                            name = dataProduct.name,
                            price = dataProduct.price.toInt(),
                            categoryId = dataProduct.categoryId,
                            isDelete = false
                        )
                    )
                    _isProsesFailed.value = ValidationResult(false)
                } else {
                    val id = UUID.randomUUID().toString()
                    productRepository.insert(
                        Product(
                            id = id,
                            name = dataProduct.name,
                            price = dataProduct.price.toInt(),
                            categoryId = dataProduct.categoryId,
                            isDelete = false
                        )
                    )
                    _isProsesFailed.value = ValidationResult(false)
                }
            }
        } catch (e: Exception) {
            Log.e("bossku", e.message.toString())
            _isProsesFailed.value = ValidationResult(true, e.message.toString())
        }

    }

    fun processDelete() {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(id = stateUi.value.id)
                _isProsesDeleteFailed.value = ValidationResult(false)
            } catch (e: Exception) {
                Log.e("bossku", e.message.toString())
                _isProsesDeleteFailed.value = ValidationResult(true, e.message.toString())
            }
        }
    }

    private fun clearError() {
        _isProsesDeleteFailed.value = ValidationResult(true, "")
        _isProsesFailed.value = ValidationResult(true, "")
        _stateListCategory.value = UiState.Error("")
    }

}

class AddProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddProductViewModel::class.java)) {
            return AddProductViewModel(
                productRepository, categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}