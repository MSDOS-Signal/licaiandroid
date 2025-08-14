package com.xinhao.myapplication.data.repository

import com.xinhao.myapplication.data.local.CategoryDao
import com.xinhao.myapplication.data.model.Category
import com.xinhao.myapplication.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> = 
        categoryDao.getCategoriesByType(type)
    
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
}
