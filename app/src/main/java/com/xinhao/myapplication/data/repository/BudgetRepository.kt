package com.xinhao.myapplication.data.repository

import com.xinhao.myapplication.data.local.BudgetDao
import com.xinhao.myapplication.data.model.Budget
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

class BudgetRepository(private val budgetDao: BudgetDao) {
    
    fun getBudgetsByMonth(month: YearMonth): Flow<List<Budget>> = budgetDao.getBudgetsByDate(month.atDay(1).atStartOfDay())
    
    suspend fun getBudgetByCategoryAndMonth(category: String, month: YearMonth): Budget? =
        budgetDao.getBudgetByCategory(category)
    
    suspend fun getTotalBudgetByMonth(month: YearMonth): Budget? = budgetDao.getTotalBudget()
    
    suspend fun insertBudget(budget: Budget): Long = budgetDao.insertBudget(budget)
    
    suspend fun updateBudget(budget: Budget) = budgetDao.updateBudget(budget)
    
    suspend fun deleteBudget(budget: Budget) = budgetDao.deleteBudget(budget)
    
    suspend fun getBudgetById(id: Long): Budget? = budgetDao.getBudgetById(id)
}
