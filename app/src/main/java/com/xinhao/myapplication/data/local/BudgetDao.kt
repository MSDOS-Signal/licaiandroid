package com.xinhao.myapplication.data.local

import androidx.room.*
import com.xinhao.myapplication.data.model.Budget
import com.xinhao.myapplication.data.model.BudgetUsage
import com.xinhao.myapplication.data.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budgets ORDER BY createdAt DESC")
    fun getAllBudgets(): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveBudgets(): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE category = :category AND isActive = 1 LIMIT 1")
    suspend fun getBudgetByCategory(category: String?): Budget?
    
    @Query("SELECT * FROM budgets WHERE period = :period AND isActive = 1")
    fun getBudgetsByPeriod(period: BudgetPeriod): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE startDate <= :date AND endDate >= :date AND isActive = 1")
    fun getBudgetsByDate(date: LocalDateTime): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Long): Budget?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<Budget>)
    
    @Update
    suspend fun updateBudget(budget: Budget)
    
    @Delete
    suspend fun deleteBudget(budget: Budget)
    
    @Query("UPDATE budgets SET isActive = :isActive WHERE id = :id")
    suspend fun updateBudgetStatus(id: Long, isActive: Boolean)
    
    @Query("UPDATE budgets SET amount = :amount WHERE id = :id")
    suspend fun updateBudgetAmount(id: Long, amount: Double)
    
    @Query("UPDATE budgets SET endDate = :endDate WHERE id = :id")
    suspend fun updateBudgetEndDate(id: Long, endDate: LocalDateTime)
    
    @Query("SELECT COUNT(*) FROM budgets WHERE isActive = 1")
    suspend fun getActiveBudgetCount(): Int
    
    @Query("SELECT * FROM budgets WHERE category IS NULL AND isActive = 1 LIMIT 1")
    suspend fun getTotalBudget(): Budget?
    
    // 获取预算使用情况（需要与TransactionDao联合查询）
    @Query("""
        SELECT b.id, b.category, b.amount, b.period, b.startDate, b.endDate, b.isActive, b.createdAt, b.updatedAt,
               COALESCE(SUM(t.amount), 0) as usedAmount,
               (b.amount - COALESCE(SUM(t.amount), 0)) as remainingAmount,
               CASE 
                   WHEN b.amount > 0 THEN (COALESCE(SUM(t.amount), 0) / b.amount) * 100
                   ELSE 0 
               END as usagePercentage
        FROM budgets b
        LEFT JOIN transactions t ON (b.category IS NULL OR b.category = t.category) 
            AND t.type = 'EXPENSE' 
            AND t.date >= b.startDate 
            AND t.date <= b.endDate
        WHERE b.isActive = 1
        GROUP BY b.id
        ORDER BY b.createdAt DESC
    """)
    fun getBudgetUsage(): Flow<List<BudgetUsage>>
}
