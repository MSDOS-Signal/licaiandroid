package com.xinhao.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String = "default_icon",
    val color: String = "#2196F3",
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

// 分类统计信息 - 用于Room查询结果
data class CategoryWithStats(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val color: String,
    val isDefault: Boolean,
    val isActive: Boolean,
    val sortOrder: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val totalAmount: Double,
    val transactionCount: Int,
    val lastUsed: LocalDateTime?
)

// 预设分类数据
object DefaultCategories {
    val expenseCategories = listOf(
        Category(name = "餐饮", type = TransactionType.EXPENSE, icon = "restaurant", color = "#FF5722", isDefault = true, sortOrder = 1),
        Category(name = "交通", type = TransactionType.EXPENSE, icon = "directions_car", color = "#2196F3", isDefault = true, sortOrder = 2),
        Category(name = "购物", type = TransactionType.EXPENSE, icon = "shopping_cart", color = "#9C27B0", isDefault = true, sortOrder = 3),
        Category(name = "娱乐", type = TransactionType.EXPENSE, icon = "movie", color = "#FF9800", isDefault = true, sortOrder = 4),
        Category(name = "医疗", type = TransactionType.EXPENSE, icon = "local_hospital", color = "#4CAF50", isDefault = true, sortOrder = 5),
        Category(name = "教育", type = TransactionType.EXPENSE, icon = "school", color = "#607D8B", isDefault = true, sortOrder = 6),
        Category(name = "住房", type = TransactionType.EXPENSE, icon = "home", color = "#795548", isDefault = true, sortOrder = 7),
        Category(name = "其他", type = TransactionType.EXPENSE, icon = "more_horiz", color = "#9E9E9E", isDefault = true, sortOrder = 8)
    )
    
    val incomeCategories = listOf(
        Category(name = "工资", type = TransactionType.INCOME, icon = "account_balance_wallet", color = "#4CAF50", isDefault = true, sortOrder = 1),
        Category(name = "奖金", type = TransactionType.INCOME, icon = "stars", color = "#8BC34A", isDefault = true, sortOrder = 2),
        Category(name = "投资", type = TransactionType.INCOME, icon = "trending_up", color = "#009688", isDefault = true, sortOrder = 3),
        Category(name = "兼职", type = TransactionType.INCOME, icon = "work", color = "#00BCD4", isDefault = true, sortOrder = 4),
        Category(name = "其他", type = TransactionType.INCOME, icon = "add_circle", color = "#9E9E9E", isDefault = true, sortOrder = 5)
    )
}
