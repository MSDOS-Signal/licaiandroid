package com.xinhao.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // 货币代码，如 USD, CNY, EUR
    val name: String, // 货币名称，如 美元, 人民币, 欧元
    val symbol: String, // 货币符号，如 $, ¥, €
    val exchangeRate: Double = 1.0, // 相对于基准货币的汇率
    val isBase: Boolean = false, // 是否为基准货币
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

// 预设货币数据
object DefaultCurrencies {
    val currencies = listOf(
        Currency(code = "CNY", name = "人民币", symbol = "¥", isBase = true, sortOrder = 1),
        Currency(code = "USD", name = "美元", symbol = "$", exchangeRate = 0.14, sortOrder = 2),
        Currency(code = "EUR", name = "欧元", symbol = "€", exchangeRate = 0.13, sortOrder = 3),
        Currency(code = "JPY", name = "日元", symbol = "¥", exchangeRate = 20.5, sortOrder = 4),
        Currency(code = "GBP", name = "英镑", symbol = "£", exchangeRate = 0.11, sortOrder = 5),
        Currency(code = "HKD", name = "港币", symbol = "HK$", exchangeRate = 1.1, sortOrder = 6),
        Currency(code = "KRW", name = "韩元", symbol = "₩", exchangeRate = 180.0, sortOrder = 7)
    )
}

// 货币转换器
object CurrencyConverter {
    fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        if (fromCurrency.code == toCurrency.code) return amount
        return amount * (toCurrency.exchangeRate / fromCurrency.exchangeRate)
    }
    
    fun formatAmount(amount: Double, currency: Currency): String {
        return "${currency.symbol}${String.format("%.2f", amount)}"
    }
}
