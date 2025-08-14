package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.ui.components.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    onTransactionAdded: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDateTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "add_transaction_animation")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
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
                        start = Offset(backgroundOffset, 0f),
                        end = Offset(1f - backgroundOffset, 1f)
                    )
                )
        )
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "添加交易记录",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                if (amount.isNotEmpty() && category.isNotEmpty()) {
                                    val transaction = Transaction(
                                        type = transactionType,
                                        amount = amount.toDoubleOrNull() ?: 0.0,
                                        category = category,
                                        date = selectedDate,
                                        note = note
                                    )
                                    onTransactionAdded(transaction)
                                    onNavigateBack()
                                }
                            },
                            enabled = amount.isNotEmpty() && category.isNotEmpty()
                        ) {
                            Text("保存")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 交易类型选择
                TransactionTypeCard(
                    transactionType = transactionType,
                    onTypeChanged = { transactionType = it }
                )
                
                // 金额输入
                AmountInputCard(
                    amount = amount,
                    onAmountChanged = { amount = it }
                )
                
                // 分类选择
                CategorySelectionCard(
                    category = category,
                    transactionType = transactionType,
                    onCategoryClick = { showCategoryDialog = true }
                )
                
                // 日期选择
                DateSelectionCard(
                    selectedDate = selectedDate,
                    onDateClick = { showDatePicker = true }
                )
                
                // 备注输入
                NoteInputCard(
                    note = note,
                    onNoteChanged = { note = it }
                )
            }
        }
    }
    
    // 分类选择对话框
    if (showCategoryDialog) {
        CategorySelectionDialog(
            transactionType = transactionType,
            onCategorySelected = { selectedCategory ->
                category = selectedCategory
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
    
    // 日期选择对话框
    if (showDatePicker) {
        DateSelectionDialog(
            currentDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun TransactionTypeCard(
    transactionType: TransactionType,
    onTypeChanged: (TransactionType) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "type_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "type_scale"
    )
    
    GradientCard(
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "交易类型",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TransactionTypeButton(
                    type = TransactionType.EXPENSE,
                    isSelected = transactionType == TransactionType.EXPENSE,
                    onClick = { onTypeChanged(TransactionType.EXPENSE) },
                    modifier = Modifier.weight(1f)
                )
                
                TransactionTypeButton(
                    type = TransactionType.INCOME,
                    isSelected = transactionType == TransactionType.INCOME,
                    onClick = { onTypeChanged(TransactionType.INCOME) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TransactionTypeButton(
    type: TransactionType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (type == TransactionType.EXPENSE) Color(0xFFF44336) else Color(0xFF4CAF50)
    val icon = if (type == TransactionType.EXPENSE) Icons.Default.Close else Icons.Default.Add
    
    Box(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 2.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color.copy(alpha = 0.1f)
            )
            .background(
                brush = if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.1f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (type == TransactionType.EXPENSE) "支出" else "收入",
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AmountInputCard(
    amount: String,
    onAmountChanged: (String) -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "金额",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = amount,
                onValueChange = { 
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onAmountChanged(it)
                    }
                },
                label = { Text("请输入金额") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
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
}

@Composable
fun CategorySelectionCard(
    category: String,
    transactionType: TransactionType,
    onCategoryClick: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "分类",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = category,
                onValueChange = { },
                label = { Text("选择分类") },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onCategoryClick) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "选择分类")
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
}

@Composable
fun DateSelectionCard(
    selectedDate: LocalDateTime,
    onDateClick: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "日期",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                onValueChange = { },
                label = { Text("选择日期") },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onDateClick) {
                        Icon(Icons.Default.Edit, contentDescription = "选择日期")
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
}

@Composable
fun NoteInputCard(
    note: String,
    onNoteChanged: (String) -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "备注",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChanged,
                label = { Text("添加备注（可选）") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun CategorySelectionDialog(
    transactionType: TransactionType,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val expenseCategories = listOf("餐饮", "交通", "购物", "娱乐", "医疗", "教育", "住房", "其他")
    val incomeCategories = listOf("工资", "奖金", "投资", "兼职", "其他")
    
    val categories = if (transactionType == TransactionType.EXPENSE) expenseCategories else incomeCategories
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "选择分类",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                categories.forEach { category ->
                    TextButton(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            category,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun DateSelectionDialog(
    currentDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(currentDate) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "选择日期",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                Text(
                    "当前选择: ${selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedDate = LocalDateTime.now() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("现在")
                    }
                    Button(
                        onClick = { selectedDate = LocalDateTime.now().minusDays(1) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("昨天")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(selectedDate) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("取消")
            }
        }
    )
}
