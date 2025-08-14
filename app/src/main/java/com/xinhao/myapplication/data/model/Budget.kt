package com.xinhao.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String? = null, // null表示总预算
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

enum class BudgetPeriod {
    DAILY,      // 每日
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY      // 每年
}

// 预算使用情况 - 用于Room查询结果
data class BudgetUsage(
    val id: Long,
    val category: String?,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val usedAmount: Double,
    val remainingAmount: Double,
    val usagePercentage: Double
) {
    val isOverBudget: Boolean
        get() = usedAmount > amount
    
    val isNearLimit: Boolean
        get() = usagePercentage >= 0.8
}
