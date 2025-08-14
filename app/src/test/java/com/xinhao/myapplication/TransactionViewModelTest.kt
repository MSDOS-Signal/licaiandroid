package com.xinhao.myapplication

import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.data.repository.TransactionRepository
import com.xinhao.myapplication.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    @Mock
    private lateinit var mockRepository: TransactionRepository

    private lateinit var viewModel: TransactionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // 设置模拟数据
        val testTransactions = listOf(
            Transaction(
                id = 1,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                category = "餐饮",
                date = LocalDateTime.now(),
                note = "午餐"
            ),
            Transaction(
                id = 2,
                type = TransactionType.INCOME,
                amount = 5000.0,
                category = "工资",
                date = LocalDateTime.now(),
                note = "月薪"
            )
        )
        
        whenever(mockRepository.getAllTransactions()).thenReturn(flowOf(testTransactions))
        whenever(mockRepository.getTotalByTypeAndDateRange(TransactionType.INCOME, any(), any())).thenReturn(flowOf(5000.0))
        whenever(mockRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, any(), any())).thenReturn(flowOf(100.0))
        whenever(mockRepository.getCategoryTotals(TransactionType.EXPENSE, any(), any())).thenReturn(flowOf(emptyList()))
        whenever(mockRepository.getCategoryTotals(TransactionType.INCOME, any(), any())).thenReturn(flowOf(emptyList()))
        
        viewModel = TransactionViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() = runTest {
        val uiState = viewModel.uiState.value
        
        assertNotNull(uiState)
        assertEquals(2, uiState.transactions.size)
        assertEquals(5000.0, uiState.monthlySummary.income)
        assertEquals(100.0, uiState.monthlySummary.expense)
        assertEquals(4900.0, uiState.monthlySummary.balance)
    }

    @Test
    fun `test add transaction`() = runTest {
        val newTransaction = Transaction(
            type = TransactionType.EXPENSE,
            amount = 50.0,
            category = "交通",
            date = LocalDateTime.now(),
            note = "公交车"
        )
        
        viewModel.addTransaction(newTransaction)
        
        // 验证repository方法被调用
        // 这里可以添加更多验证逻辑
    }

    @Test
    fun `test delete transaction`() = runTest {
        val transactionToDelete = Transaction(
            id = 1,
            type = TransactionType.EXPENSE,
            amount = 100.0,
            category = "餐饮",
            date = LocalDateTime.now(),
            note = "午餐"
        )
        
        viewModel.deleteTransaction(transactionToDelete)
        
        // 验证repository方法被调用
        // 这里可以添加更多验证逻辑
    }

    @Test
    fun `test update transaction`() = runTest {
        val updatedTransaction = Transaction(
            id = 1,
            type = TransactionType.EXPENSE,
            amount = 150.0,
            category = "餐饮",
            date = LocalDateTime.now(),
            note = "午餐和晚餐"
        )
        
        viewModel.updateTransaction(updatedTransaction)
        
        // 验证repository方法被调用
        // 这里可以添加更多验证逻辑
    }
}
