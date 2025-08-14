package com.xinhao.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val date: LocalDateTime,
    val note: String = "",
    val imagePath: String? = null,
    val location: String? = null,
    val tags: String? = null, // 逗号分隔的标签
    val isRecurring: Boolean = false,
    val recurringPattern: String? = null, // 重复模式
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class TransactionWithImages(
    val transaction: Transaction,
    val images: List<TransactionImage>
)

@Entity(tableName = "transaction_images")
data class TransactionImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: Long,
    val imagePath: String,
    val thumbnailPath: String? = null,
    val description: String? = null,
    val createdAt: LocalDateTime? = null
)

// 重复交易模式
enum class RecurringPattern {
    DAILY,      // 每日
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY      // 每年
}

// 交易标签
data class TransactionTag(
    val name: String,
    val color: String,
    val count: Int = 0
)
