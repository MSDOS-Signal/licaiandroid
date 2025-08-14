package com.xinhao.myapplication.data.local

import androidx.room.*
import com.xinhao.myapplication.data.model.Category
import com.xinhao.myapplication.data.model.CategoryWithStats
import com.xinhao.myapplication.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC")
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE isDefault = 1")
    fun getDefaultCategories(): Flow<List<Category>>
    
    @Query("SELECT c.id, c.name, c.type, c.icon, c.color, c.isDefault, c.isActive, c.sortOrder, c.createdAt, c.updatedAt, " +
           "COALESCE(SUM(t.amount), 0) as totalAmount, " +
           "COUNT(t.id) as transactionCount, " +
           "MAX(t.date) as lastUsed " +
           "FROM categories c " +
           "LEFT JOIN transactions t ON c.name = t.category AND c.type = t.type " +
           "WHERE c.type = :type AND c.isActive = 1 " +
           "GROUP BY c.id " +
           "ORDER BY totalAmount DESC")
    fun getCategoriesWithStats(type: TransactionType): Flow<List<CategoryWithStats>>
    
    @Query("SELECT * FROM categories WHERE name = :name AND type = :type LIMIT 1")
    suspend fun getCategoryByName(name: String, type: TransactionType): Category?
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("UPDATE categories SET isActive = :isActive WHERE id = :id")
    suspend fun updateCategoryStatus(id: Long, isActive: Boolean)
    
    @Query("UPDATE categories SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateCategorySortOrder(id: Long, sortOrder: Int)
    
    @Query("SELECT COUNT(*) FROM categories WHERE type = :type")
    suspend fun getCategoryCountByType(type: TransactionType): Int
    
    @Query("SELECT MAX(sortOrder) FROM categories WHERE type = :type")
    suspend fun getMaxSortOrderByType(type: TransactionType): Int?
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%' AND type = :type")
    fun searchCategories(query: String, type: TransactionType): Flow<List<Category>>
}
