package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.ui.components.*
import com.xinhao.myapplication.ui.viewmodel.TransactionUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    uiState: TransactionUiState,
    onNavigateBack: () -> Unit,
    onTransactionDeleted: (Transaction) -> Unit,
    onTransactionEdited: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Transaction?>(null) }
    var showEditDialog by remember { mutableStateOf<Transaction?>(null) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "transactions_animation")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "background_offset"
    )
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 动态背景渐变
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.02f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.01f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(backgroundOffset, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f - backgroundOffset, 1f)
                    )
                )
        )
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "交易记录",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "筛选")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 搜索栏
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 统计信息
                TransactionStats(uiState)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 交易列表
                TransactionList(
                    transactions = filterTransactions(uiState.transactions, searchQuery, selectedType),
                    onTransactionDeleted = { transaction ->
                        showDeleteConfirmDialog = transaction
                    },
                    onTransactionEdited = { transaction ->
                        showEditDialog = transaction
                    }
                )
            }
        }
    }
    
    // 筛选对话框
    if (showFilterDialog) {
        FilterDialog(
            selectedType = selectedType,
            onTypeSelected = { selectedType = it },
            onDismiss = { showFilterDialog = false }
        )
    }
    
    // 删除确认对话框
    if (showDeleteConfirmDialog != null) {
        DeleteConfirmDialog(
            transaction = showDeleteConfirmDialog!!,
            onConfirm = { transaction ->
                onTransactionDeleted(transaction)
                showDeleteConfirmDialog = null
            },
            onDismiss = { showDeleteConfirmDialog = null }
        )
    }
    
    // 编辑对话框
    if (showEditDialog != null) {
        EditTransactionDialog(
            transaction = showEditDialog!!,
            onSave = { updatedTransaction ->
                onTransactionEdited(updatedTransaction)
                showEditDialog = null
            },
            onDismiss = { showEditDialog = null }
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CardContainer(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("搜索交易记录...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun TransactionStats(uiState: TransactionUiState) {
    CardContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OverviewItem(
                title = "总收入",
                value = uiState.monthlySummary.income,
                color = Color(0xFF4CAF50),
                icon = Icons.Default.Add
            )
            
            OverviewItem(
                title = "总支出",
                value = uiState.monthlySummary.expense,
                color = Color(0xFFF44336),
                icon = Icons.Default.Close
            )
            
            OverviewItem(
                title = "交易数",
                value = uiState.transactions.size.toDouble(),
                color = Color(0xFF2196F3),
                icon = Icons.Default.List
            )
        }
    }
}

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onTransactionDeleted: (Transaction) -> Unit,
    onTransactionEdited: (Transaction) -> Unit
) {
    if (transactions.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Receipt,
            title = "暂无交易记录",
            message = "没有找到匹配的交易记录"
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onDelete = { onTransactionDeleted(transaction) },
                    onEdit = { onTransactionEdited(transaction) }
                )
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardContainer(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CategoryIcon(
                    iconName = transaction.category,
                    color = if (transaction.type == TransactionType.INCOME) 
                        Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (transaction.note.isNotEmpty()) {
                        Text(
                            text = transaction.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    
                    DateText(
                        date = transaction.date,
                        pattern = "MM-dd HH:mm"
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                AmountText(
                    amount = transaction.amount,
                    color = if (transaction.type == TransactionType.INCOME) 
                        Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "筛选交易",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                TextButton(
                    onClick = { onTypeSelected(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "全部",
                        fontWeight = if (selectedType == null) FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                TextButton(
                    onClick = { onTypeSelected(TransactionType.INCOME) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "收入",
                        fontWeight = if (selectedType == TransactionType.INCOME) FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                TextButton(
                    onClick = { onTypeSelected(TransactionType.EXPENSE) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "支出",
                        fontWeight = if (selectedType == TransactionType.EXPENSE) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
fun DeleteConfirmDialog(
    transaction: Transaction,
    onConfirm: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "确认删除",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Text("确定要删除这笔交易记录吗？此操作无法撤销。")
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(transaction) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var note by remember { mutableStateOf(transaction.note) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "编辑交易",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = it
                        }
                    },
                    label = { Text("金额") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedTransaction = transaction.copy(
                        amount = amount.toDoubleOrNull() ?: transaction.amount,
                        note = note
                    )
                    onSave(updatedTransaction)
                },
                enabled = amount.isNotEmpty()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun filterTransactions(
    transactions: List<Transaction>,
    searchQuery: String,
    selectedType: TransactionType?
): List<Transaction> {
    return transactions.filter { transaction ->
        val matchesQuery = searchQuery.isEmpty() || 
            transaction.category.contains(searchQuery, ignoreCase = true) ||
            transaction.note.contains(searchQuery, ignoreCase = true)
        
        val matchesType = selectedType == null || transaction.type == selectedType
        
        matchesQuery && matchesType
    }.sortedByDescending { it.date }
}
