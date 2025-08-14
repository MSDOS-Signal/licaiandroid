package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
import com.xinhao.myapplication.ui.components.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showEditBudgetDialog by remember { mutableStateOf<BudgetItem?>(null) }
    var showDeleteBudgetDialog by remember { mutableStateOf<BudgetItem?>(null) }
    
    // 模拟预算数据
    val budgets = remember {
        listOf(
            BudgetItem(
                id = 1,
                category = "总预算",
                amount = 5000.0,
                used = 3200.0,
                remaining = 1800.0,
                percentage = 0.64f
            ),
            BudgetItem(
                id = 2,
                category = "餐饮",
                amount = 1500.0,
                used = 1200.0,
                remaining = 300.0,
                percentage = 0.80f
            ),
            BudgetItem(
                id = 3,
                category = "交通",
                amount = 800.0,
                used = 450.0,
                remaining = 350.0,
                percentage = 0.56f
            ),
            BudgetItem(
                id = 4,
                category = "购物",
                amount = 1000.0,
                used = 800.0,
                remaining = 200.0,
                percentage = 0.80f
            )
        )
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "budget_animation")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
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
                            "预算管理",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddBudgetDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "添加预算")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 总预算概览
                item {
                    TotalBudgetOverview(budgets)
                }
                
                // 分类预算列表
                items(budgets.filter { it.category != "总预算" }) { budget ->
                    BudgetCard(
                        budget = budget,
                        onEdit = { showEditBudgetDialog = budget },
                        onDelete = { showDeleteBudgetDialog = budget }
                    )
                }
                
                // 预算建议
                item {
                    BudgetSuggestions(budgets)
                }
            }
        }
    }
    
    // 添加预算对话框
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onBudgetAdded = { /* TODO: 添加预算逻辑 */ },
            onDismiss = { showAddBudgetDialog = false }
        )
    }
    
    // 编辑预算对话框
    showEditBudgetDialog?.let { budget ->
        EditBudgetDialog(
            budget = budget,
            onBudgetUpdated = { /* TODO: 更新预算逻辑 */ },
            onDismiss = { showEditBudgetDialog = null }
        )
    }
    
    // 删除预算确认对话框
    showDeleteBudgetDialog?.let { budget ->
        DeleteBudgetDialog(
            budget = budget,
            onBudgetDeleted = { /* TODO: 删除预算逻辑 */ },
            onDismiss = { showDeleteBudgetDialog = null }
        )
    }
}

@Composable
fun TotalBudgetOverview(budgets: List<BudgetItem>) {
    val totalBudget = budgets.find { it.category == "总预算" }
    
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "本月总预算",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            totalBudget?.let { budget ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BudgetStatItem(
                        title = "总预算",
                        value = budget.amount,
                        color = MaterialTheme.colorScheme.primary
                    )
                    BudgetStatItem(
                        title = "已使用",
                        value = budget.used,
                        color = Color(0xFFF44336)
                    )
                    BudgetStatItem(
                        title = "剩余",
                        value = budget.remaining,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedProgressBar(
                    progress = budget.percentage,
                    color = if (budget.percentage > 0.8f) Color(0xFFF44336) else Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${(budget.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BudgetStatItem(
    title: String,
    value: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AmountText(
            amount = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BudgetCard(
    budget: BudgetItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = budget.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "预算: ${formatCurrency(budget.amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = Color(0xFFF44336)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "已使用: ${formatCurrency(budget.used)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF44336)
                )
                Text(
                    text = "剩余: ${formatCurrency(budget.remaining)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AnimatedProgressBar(
                progress = budget.percentage,
                color = if (budget.percentage > 0.8f) Color(0xFFF44336) else Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun BudgetSuggestions(budgets: List<BudgetItem>) {
    val overBudget = budgets.filter { it.percentage > 1.0f }
    val nearLimit = budgets.filter { it.percentage > 0.8f && it.percentage <= 1.0f }
    
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "预算建议",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (overBudget.isNotEmpty()) {
                Text(
                    text = "⚠️ 预算超支",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
                overBudget.forEach { budget ->
                    Text(
                        text = "• ${budget.category}: 已超支 ${formatCurrency(budget.used - budget.amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF44336)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (nearLimit.isNotEmpty()) {
                Text(
                    text = "⚠️ 预算接近限制",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
                nearLimit.forEach { budget ->
                    Text(
                        text = "• ${budget.category}: 剩余 ${formatCurrency(budget.remaining)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF9800)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (overBudget.isEmpty() && nearLimit.isEmpty()) {
                Text(
                    text = "✅ 预算控制良好",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "继续保持当前的支出控制！",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    onBudgetAdded: (BudgetItem) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "添加预算",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("分类") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = it
                        }
                    },
                    label = { Text("预算金额") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (category.isNotEmpty() && amountValue != null && amountValue > 0) {
                        val budget = BudgetItem(
                            id = 0,
                            category = category,
                            amount = amountValue,
                            used = 0.0,
                            remaining = amountValue,
                            percentage = 0f
                        )
                        onBudgetAdded(budget)
                    }
                },
                enabled = category.isNotEmpty() && amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
            ) {
                Text("添加")
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
fun EditBudgetDialog(
    budget: BudgetItem,
    onBudgetUpdated: (BudgetItem) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf(budget.amount.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "编辑预算",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "分类: ${budget.category}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = it
                        }
                    },
                    label = { Text("预算金额") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        val updatedBudget = budget.copy(amount = amountValue)
                        onBudgetUpdated(updatedBudget)
                    }
                },
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
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

@Composable
fun DeleteBudgetDialog(
    budget: BudgetItem,
    onBudgetDeleted: (BudgetItem) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "删除预算",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            ) 
        },
        text = {
            Text("确定要删除 ${budget.category} 的预算吗？此操作无法撤销。")
        },
        confirmButton = {
            TextButton(
                onClick = { onBudgetDeleted(budget) },
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

data class BudgetItem(
    val id: Int,
    val category: String,
    val amount: Double,
    val used: Double,
    val remaining: Double,
    val percentage: Float
)
