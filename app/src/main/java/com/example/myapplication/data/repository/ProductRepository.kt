package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.ProductDao
import com.example.myapplication.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> =
        productDao.getProductsByCategory(categoryId)

    fun getProductById(id: Long) = productDao.getProductById(id)
}