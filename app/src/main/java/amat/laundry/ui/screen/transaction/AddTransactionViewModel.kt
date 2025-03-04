package amat.laundry.ui.screen.transaction

import amat.laundry.data.repository.CategoryRepository
import amat.laundry.data.repository.ProductRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddTransactionViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
}

class AddTransactionViewModelFactory(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            return AddTransactionViewModel(
                productRepository, categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}

