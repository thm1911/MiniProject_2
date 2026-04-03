package com.example.myapplication.ui.category

import com.example.myapplication.data.local.entity.CategoryEntity
import com.example.myapplication.data.local.entity.ProductEntity

data class CategorySectionUi(
    val category: CategoryEntity,
    val previewProducts: List<ProductEntity>
)