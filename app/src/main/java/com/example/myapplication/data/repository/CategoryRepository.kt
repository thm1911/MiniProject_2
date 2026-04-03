package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.CategoryDao
import com.example.myapplication.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
}