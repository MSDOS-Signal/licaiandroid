package com.xinhao.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xinhao.myapplication.data.local.AppDatabase
import com.xinhao.myapplication.data.repository.TransactionRepository
import com.xinhao.myapplication.ui.components.AddTransactionDialog
import com.xinhao.myapplication.ui.screens.*
import com.xinhao.myapplication.ui.theme.MyApplication2Theme
import com.xinhao.myapplication.ui.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化数据库和Repository
        val database = AppDatabase.getDatabase(this)
        val transactionRepository = TransactionRepository(database.transactionDao())
        
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            MyApplication2Theme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FinanceApp(
                        transactionRepository = transactionRepository,
                        isDarkMode = isDarkMode,
                        onDarkModeChanged = { isDarkMode = it }
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceApp(
    transactionRepository: TransactionRepository,
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {},
    transactionViewModel: TransactionViewModel = viewModel { 
        TransactionViewModel(transactionRepository) 
    }
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    val uiState by transactionViewModel.uiState.collectAsState()
    
    // 处理错误状态
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            // TODO: 显示错误提示
            println("Error: $error")
        }
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("记录") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text("报表") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    label = { Text("预算") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("设置") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0 || selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddTransactionDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加交易")
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> {
                HomeScreen(
                    uiState = uiState,
                    onNavigateToAddTransaction = { showAddTransactionDialog = true },
                    onNavigateToTransactions = { selectedTab = 1 },
                    onNavigateToReports = { selectedTab = 2 },
                    onNavigateToSettings = { selectedTab = 4 },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            1 -> {
                TransactionsScreen(
                    uiState = uiState,
                    onNavigateBack = { selectedTab = 0 },
                    onTransactionDeleted = { transaction ->
                        transactionViewModel.deleteTransaction(transaction)
                    },
                    onTransactionEdited = { transaction ->
                        transactionViewModel.updateTransaction(transaction)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            2 -> {
                ReportsScreen(
                    uiState = uiState,
                    onNavigateBack = { selectedTab = 0 },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            3 -> {
                BudgetScreen(
                    onNavigateBack = { selectedTab = 0 },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            4 -> {
                SettingsScreen(
                    onNavigateBack = { selectedTab = 0 },
                    isDarkMode = isDarkMode,
                    onDarkModeChanged = onDarkModeChanged,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        // 添加交易对话框
        if (showAddTransactionDialog) {
            AddTransactionDialog(
                onTransactionAdded = { transaction ->
                    transactionViewModel.addTransaction(transaction)
                    showAddTransactionDialog = false
                },
                onDismiss = { showAddTransactionDialog = false }
            )
        }
    }
}