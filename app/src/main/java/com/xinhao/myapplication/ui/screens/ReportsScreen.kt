package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
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
import com.xinhao.myapplication.data.local.CategoryTotal
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.ui.components.*
import com.xinhao.myapplication.ui.viewmodel.TransactionUiState
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    uiState: TransactionUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("本月") }
    var showPeriodSelector by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "reports_animation")
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
                            "财务报表",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        TextButton(onClick = { showPeriodSelector = true }) {
                            Text(selectedPeriod)
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
                // 概览卡片
                item {
                    OverviewCard(uiState)
                }
                
                // 收支趋势
                item {
                    TrendCard(uiState)
                }
                
                // 分类分析
                item {
                    CategoryAnalysisCard(
                        title = "支出分类",
                        categories = uiState.monthlySummary.expenseCategories,
                        total = uiState.monthlySummary.expense,
                        color = Color(0xFFF44336)
                    )
                }
                
                item {
                    CategoryAnalysisCard(
                        title = "收入分类",
                        categories = uiState.monthlySummary.incomeCategories,
                        total = uiState.monthlySummary.income,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                // 月度对比
                item {
                    MonthlyComparisonCard(uiState)
                }
            }
        }
    }
    
    // 时间段选择对话框
    if (showPeriodSelector) {
        PeriodSelectorDialog(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it },
            onDismiss = { showPeriodSelector = false }
        )
    }
}

@Composable
fun OverviewCard(uiState: TransactionUiState) {
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
                text = "财务概览",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem(
                    title = "总收入",
                    value = uiState.monthlySummary.income,
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.TrendingUp
                )
                
                OverviewItem(
                    title = "总支出",
                    value = uiState.monthlySummary.expense,
                    color = Color(0xFFF44336),
                    icon = Icons.Default.TrendingDown
                )
                
                OverviewItem(
                    title = "净收入",
                    value = uiState.monthlySummary.balance,
                    color = if (uiState.monthlySummary.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                    icon = Icons.Filled.AccountBalance
                )
            }
        }
    }
}

@Composable
fun OverviewItem(
    title: String,
    value: Double,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = color.copy(alpha = 0.1f)
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AmountText(
            amount = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TrendCard(uiState: TransactionUiState) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "收支趋势",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简单的趋势指示器
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrendIndicator(
                    title = "收入趋势",
                    trend = if (uiState.monthlySummary.income > 0) "上升" else "持平",
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.TrendingUp
                )
                
                TrendIndicator(
                    title = "支出趋势",
                    trend = if (uiState.monthlySummary.expense > 0) "上升" else "持平",
                    color = Color(0xFFF44336),
                    icon = Icons.Default.TrendingDown
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 预算使用情况
            if (uiState.monthlySummary.expense > 0) {
                Text(
                    text = "预算使用情况",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val progress = (uiState.monthlySummary.expense / 5000.0).coerceAtMost(1.0).toFloat()
                AnimatedProgressBar(
                    progress = progress,
                    color = if (progress > 0.8f) Color(0xFFF44336) else Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "已使用: ${formatCurrency(uiState.monthlySummary.expense)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "预算: ${formatCurrency(5000.0)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TrendIndicator(
    title: String,
    trend: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = trend,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryAnalysisCard(
    title: String,
    categories: List<CategoryTotal>,
    total: Double,
    color: Color
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (categories.isEmpty()) {
                EmptyState(
                icon = Icons.Default.PieChart,
                    title = "暂无数据",
                    message = "该时间段内没有相关记录"
                )
            } else {
                categories.forEach { category ->
                    CategoryProgressItem(
                        category = category,
                        total = total,
                        color = color
                    )
                    
                    if (category != categories.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryProgressItem(
    category: CategoryTotal,
    total: Double,
    color: Color
) {
    val progress = if (total > 0) (category.total / total).toFloat() else 0f
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            AmountText(
                amount = category.total,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MonthlyComparisonCard(uiState: TransactionUiState) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "月度对比",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简单的月度对比数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ComparisonItem(
                    title = "本月",
                    income = uiState.monthlySummary.income,
                    expense = uiState.monthlySummary.expense
                )
                
                ComparisonItem(
                    title = "上月",
                    income = uiState.monthlySummary.income * 0.9, // 模拟数据
                    expense = uiState.monthlySummary.expense * 0.95
                )
            }
        }
    }
}

@Composable
fun ComparisonItem(
    title: String,
    income: Double,
    expense: Double
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AmountText(
            amount = income,
            color = Color(0xFF4CAF50),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "收入",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AmountText(
            amount = expense,
            color = Color(0xFFF44336),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "支出",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PeriodSelectorDialog(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val periods = listOf("本周", "本月", "本季度", "本年")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "选择时间段",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                periods.forEach { period ->
                    TextButton(
                        onClick = { 
                            onPeriodSelected(period)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            period,
                            fontWeight = if (period == selectedPeriod) FontWeight.Bold else FontWeight.Normal
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
