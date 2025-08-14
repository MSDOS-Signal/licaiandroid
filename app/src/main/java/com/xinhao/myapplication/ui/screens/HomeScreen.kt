package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.xinhao.myapplication.ui.components.*
import com.xinhao.myapplication.ui.viewmodel.TransactionUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: TransactionUiState,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "home_animation")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.02f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(backgroundOffset, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f - backgroundOffset, 1f)
                    )
                )
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 欢迎区域
            item {
                WelcomeSection()
            }
            
            // 月度概览
            item {
                MonthlyOverview(uiState)
            }
            
            // 快捷操作
            item {
                QuickActionsSection(
                    onAddTransaction = onNavigateToAddTransaction,
                    onViewTransactions = onNavigateToTransactions,
                    onViewReports = onNavigateToReports,
                    onViewBudget = { /* TODO: 导航到预算页面 */ }
                )
            }
            
            // 最近交易
            item {
                RecentTransactionsSection(
                    transactions = uiState.transactions.take(5),
                    onViewAll = onNavigateToTransactions
                )
            }
            
            // 预算进度
            item {
                BudgetProgressSection()
            }
            
            // 财务建议
            item {
                FinancialTipsSection(uiState)
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    val currentHour = LocalDateTime.now().hour
    val greeting = when {
        currentHour < 12 -> "早上好"
        currentHour < 18 -> "下午好"
        else -> "晚上好"
    }
    
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "$greeting，欢迎回来！",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "今天是 ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE"))}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "本月交易",
                    value = "0",
                    icon = Icons.Default.Receipt,
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatCard(
                    title = "预算状态",
                    value = "良好",
                    icon = Icons.Default.AccountBalance,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
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
}

@Composable
fun MonthlyOverview(uiState: TransactionUiState) {
    val monthlySummary = uiState.monthlySummary
    val income = monthlySummary.income
    val expense = monthlySummary.expense
    val balance = income - expense
    
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "本月概览",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem(
                    title = "收入",
                    value = income,
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.TrendingUp
                )
                
                OverviewItem(
                    title = "支出",
                    value = expense,
                    color = Color(0xFFF44336),
                    icon = Icons.Default.TrendingDown
                )
                
                OverviewItem(
                    title = "结余",
                    value = balance,
                    color = if (balance >= 0) Color(0xFF2196F3) else Color(0xFFF44336),
                    icon = Icons.Default.AccountBalance
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 收支比例
            if (income > 0) {
                val expenseRatio = expense / income
                val incomeRatio = 1f - expenseRatio
                
                Column {
                    Text(
                        text = "收支比例",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "收入 ${(incomeRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "支出 ${(expenseRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onAddTransaction: () -> Unit,
    onViewTransactions: () -> Unit,
    onViewReports: () -> Unit,
    onViewBudget: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "快捷操作",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "记账",
                    onClick = onAddTransaction,
                    color = MaterialTheme.colorScheme.primary
                )
                
                QuickActionButton(
                    icon = Icons.Default.List,
                    label = "记录",
                    onClick = onViewTransactions,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Assessment,
                    label = "报表",
                    onClick = onViewReports,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                QuickActionButton(
                    icon = Icons.Default.AccountBalance,
                    label = "预算",
                    onClick = onViewBudget,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    onViewAll: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近交易",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (transactions.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Receipt,
                    title = "暂无交易记录",
                    message = "开始记录您的第一笔交易吧！"
                )
            } else {
                transactions.forEach { transaction ->
                    TransactionItem(transaction = transaction)
                    if (transaction != transactions.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(
                iconName = transaction.category,
                color = if (transaction.type == com.xinhao.myapplication.data.model.TransactionType.INCOME) 
                    Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = transaction.note.ifEmpty { transaction.date.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        AmountText(
            amount = transaction.amount,
            color = if (transaction.type == com.xinhao.myapplication.data.model.TransactionType.INCOME) 
                Color(0xFF4CAF50) else Color(0xFFF44336),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BudgetProgressSection() {
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "预算进度",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 模拟预算数据
            val budgetItems = listOf(
                BudgetProgressItem("餐饮", 1200.0, 1500.0, 0.8f),
                BudgetProgressItem("交通", 450.0, 800.0, 0.56f),
                BudgetProgressItem("购物", 800.0, 1000.0, 0.8f)
            )
            
            budgetItems.forEach { item ->
                BudgetProgressRow(item)
                if (item != budgetItems.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun BudgetProgressRow(item: BudgetProgressItem) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${formatCurrency(item.used)} / ${formatCurrency(item.total)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = item.percentage,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (item.percentage > 0.8f) Color(0xFFF44336) else Color(0xFF4CAF50),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun FinancialTipsSection(uiState: TransactionUiState) {
    val monthlySummary = uiState.monthlySummary
    val tips = generateFinancialTips(monthlySummary)
    
    CardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "财务建议",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun generateFinancialTips(monthlySummary: com.xinhao.myapplication.ui.viewmodel.MonthlySummary): List<String> {
    val tips = mutableListOf<String>()
    val income = monthlySummary.income
    val expense = monthlySummary.expense
    val balance = income - expense
    
    when {
        balance < 0 -> {
            tips.add("本月支出超过收入，建议控制不必要的开支")
            tips.add("可以考虑制定更严格的预算计划")
        }
        balance < income * 0.2 -> {
            tips.add("结余较少，建议增加储蓄比例")
            tips.add("可以尝试50/30/20法则管理支出")
        }
        else -> {
            tips.add("财务状况良好，继续保持！")
            tips.add("可以考虑投资理财增加收入")
        }
    }
    
    if (expense > income * 0.8) {
        tips.add("支出占收入比例较高，建议优化支出结构")
    }
    
    return tips
}

data class BudgetProgressItem(
    val category: String,
    val used: Double,
    val total: Double,
    val percentage: Float
)
