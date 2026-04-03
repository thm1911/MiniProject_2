package com.example.myapplication.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.CategoryRepository
import com.example.myapplication.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val sections: List<CategorySectionUi> = emptyList()
)

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                categoryRepository.getAllCategories(),
                productRepository.getAllProducts()
            ) { categories, products ->
                categories.map { cat ->
                    CategorySectionUi(
                        category = cat,
                        previewProducts = products
                            .filter { it.categoryId == cat.id }
                            .sortedByDescending { it.id }
                            .take(3)
                    )
                }
            }.collect { sections ->
                _uiState.value = CategoriesUiState(sections = sections)
            }
        }
    }
}

class CategoriesViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(categoryRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}