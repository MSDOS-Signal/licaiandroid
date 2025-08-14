package com.xinhao.myapplication.data.repository

import com.xinhao.myapplication.data.local.TransactionDao
import com.xinhao.myapplication.data.local.CategoryTotal
import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByType(type)
    
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(category)
    
    fun getTotalByTypeAndDateRange(type: TransactionType, startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double?> =
        transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    
    fun getCategoryTotals(type: TransactionType, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotals(type, startDate, endDate)
    
    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)
    
    suspend fun deleteTransactionsByIds(ids: List<Long>) =
        transactionDao.deleteTransactionsByIds(ids)
    
    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)
}
