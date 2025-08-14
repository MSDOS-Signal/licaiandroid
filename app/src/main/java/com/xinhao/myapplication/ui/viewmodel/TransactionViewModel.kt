package com.xinhao.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.data.repository.TransactionRepository
import com.xinhao.myapplication.data.local.CategoryTotal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    init {
        loadTransactions()
        loadMonthlySummary()
    }

    fun setSelectedMonth(month: YearMonth) {
        _selectedMonth.value = month
        loadMonthlySummary()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .collect { transactions ->
                    _uiState.update { it.copy(transactions = transactions) }
                }
        }
    }

    private fun loadMonthlySummary() {
        viewModelScope.launch {
            val month = _selectedMonth.value
            val startDate = month.atDay(1).atStartOfDay()
            val endDate = month.atEndOfMonth().atTime(23, 59, 59)

            combine(
                transactionRepository.getTotalByTypeAndDateRange(TransactionType.INCOME, startDate, endDate),
                transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate),
                transactionRepository.getCategoryTotals(TransactionType.EXPENSE, startDate, endDate),
                transactionRepository.getCategoryTotals(TransactionType.INCOME, startDate, endDate)
            ) { income, expense, expenseCategories, incomeCategories ->
                MonthlySummary(
                    income = income ?: 0.0,
                    expense = expense ?: 0.0,
                    balance = (income ?: 0.0) - (expense ?: 0.0),
                    expenseCategories = expenseCategories,
                    incomeCategories = incomeCategories
                )
            }.collect { summary ->
                _uiState.update { it.copy(monthlySummary = summary) }
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun deleteTransactions(ids: List<Long>) {
        viewModelScope.launch {
            transactionRepository.deleteTransactionsByIds(ids)
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionRepository.getTransactionsByType(type)
    }

    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> {
        return transactionRepository.getTransactionsByDateRange(startDate, endDate)
    }
}

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val monthlySummary: MonthlySummary = MonthlySummary(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MonthlySummary(
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val balance: Double = 0.0,
    val expenseCategories: List<CategoryTotal> = emptyList(),
    val incomeCategories: List<CategoryTotal> = emptyList()
)
