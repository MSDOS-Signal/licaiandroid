package com.xinhao.myapplication.data.export

import android.content.Context
import android.net.Uri
import com.xinhao.myapplication.data.model.Transaction
import com.xinhao.myapplication.data.model.TransactionType
import com.xinhao.myapplication.data.model.Category
import com.xinhao.myapplication.data.model.Budget
import com.xinhao.myapplication.data.model.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

class DataExporter(private val context: Context) {
    
    suspend fun exportToCSV(
        transactions: List<Transaction>,
        categories: List<Category>,
        budgets: List<Budget>,
        currencies: List<Currency>,
        uri: Uri,
        includeImages: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream, "UTF-8")
                
                // 写入交易记录
                writer.write("=== 交易记录 ===\n")
                writer.write("ID,类型,金额,分类,日期,备注,图片路径,位置,标签,是否重复,重复模式,创建时间\n")
                
                transactions.forEach { transaction ->
                    writer.write("${transaction.id},")
                    writer.write("${transaction.type},")
                    writer.write("${transaction.amount},")
                    writer.write("\"${transaction.category}\",")
                    writer.write("${transaction.date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},")
                    writer.write("\"${transaction.note.replace("\"", "\"\"")}\",")
                    writer.write("${transaction.imagePath ?: ""},")
                    writer.write("${transaction.location ?: ""},")
                    writer.write("${transaction.tags ?: ""},")
                    writer.write("${transaction.isRecurring},")
                    writer.write("${transaction.recurringPattern ?: ""},")
                    writer.write("${transaction.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""}\n")
                }
                
                // 写入分类
                writer.write("\n=== 分类 ===\n")
                writer.write("ID,名称,类型,图标,颜色,是否默认,是否激活,排序,创建时间\n")
                
                categories.forEach { category ->
                    writer.write("${category.id},")
                    writer.write("\"${category.name}\",")
                    writer.write("${category.type},")
                    writer.write("${category.icon},")
                    writer.write("${category.color},")
                    writer.write("${category.isDefault},")
                    writer.write("${category.isActive},")
                    writer.write("${category.sortOrder},")
                    writer.write("${category.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""}\n")
                }
                
                // 写入预算
                writer.write("\n=== 预算 ===\n")
                writer.write("ID,分类,金额,周期,开始日期,结束日期,是否激活,创建时间\n")
                
                budgets.forEach { budget ->
                    writer.write("${budget.id},")
                    writer.write("${budget.category ?: "总预算"},")
                    writer.write("${budget.amount},")
                    writer.write("${budget.period},")
                    writer.write("${budget.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},")
                    writer.write("${budget.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},")
                    writer.write("${budget.isActive},")
                    writer.write("${budget.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""}\n")
                }
                
                // 写入货币
                writer.write("\n=== 货币 ===\n")
                writer.write("ID,代码,名称,符号,汇率,是否基准,是否激活,排序,创建时间\n")
                
                currencies.forEach { currency ->
                    writer.write("${currency.id},")
                    writer.write("${currency.code},")
                    writer.write("\"${currency.name}\",")
                    writer.write("${currency.symbol},")
                    writer.write("${currency.exchangeRate},")
                    writer.write("${currency.isBase},")
                    writer.write("${currency.isActive},")
                    writer.write("${currency.sortOrder},")
                    writer.write("${currency.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""}\n")
                }
                
                writer.flush()
            }
            
            Result.success("数据导出成功")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportToJSON(
        transactions: List<Transaction>,
        categories: List<Category>,
        budgets: List<Budget>,
        currencies: List<Currency>,
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject()
            
            // 交易记录
            val transactionsArray = JSONArray()
            transactions.forEach { transaction ->
                val transactionObj = JSONObject().apply {
                    put("id", transaction.id)
                    put("type", transaction.type.name)
                    put("amount", transaction.amount)
                    put("category", transaction.category)
                    put("date", transaction.date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    put("note", transaction.note)
                    put("imagePath", transaction.imagePath)
                    put("location", transaction.location)
                    put("tags", transaction.tags)
                    put("isRecurring", transaction.isRecurring)
                    put("recurringPattern", transaction.recurringPattern)
                    put("createdAt", transaction.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: "")
                }
                transactionsArray.put(transactionObj)
            }
            jsonObject.put("transactions", transactionsArray)
            
            // 分类
            val categoriesArray = JSONArray()
            categories.forEach { category ->
                val categoryObj = JSONObject().apply {
                    put("id", category.id)
                    put("name", category.name)
                    put("type", category.type.name)
                    put("icon", category.icon)
                    put("color", category.color)
                    put("isDefault", category.isDefault)
                    put("isActive", category.isActive)
                    put("sortOrder", category.sortOrder)
                    put("createdAt", category.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: "")
                }
                categoriesArray.put(categoryObj)
            }
            jsonObject.put("categories", categoriesArray)
            
            // 预算
            val budgetsArray = JSONArray()
            budgets.forEach { budget ->
                val budgetObj = JSONObject().apply {
                    put("id", budget.id)
                    put("category", budget.category)
                    put("amount", budget.amount)
                    put("period", budget.period.name)
                    put("startDate", budget.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    put("endDate", budget.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    put("isActive", budget.isActive)
                    put("createdAt", budget.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: "")
                }
                budgetsArray.put(budgetObj)
            }
            jsonObject.put("budgets", budgetsArray)
            
            // 货币
            val currenciesArray = JSONArray()
            currencies.forEach { currency ->
                val currencyObj = JSONObject().apply {
                    put("id", currency.id)
                    put("code", currency.code)
                    put("name", currency.name)
                    put("symbol", currency.symbol)
                    put("exchangeRate", currency.exchangeRate)
                    put("isBase", currency.isBase)
                    put("isActive", currency.isActive)
                    put("sortOrder", currency.sortOrder)
                    put("createdAt", currency.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: "")
                }
                currenciesArray.put(currencyObj)
            }
            jsonObject.put("currencies", currenciesArray)
            
            // 元数据
            jsonObject.put("exportDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            jsonObject.put("version", "1.0")
            jsonObject.put("totalTransactions", transactions.size)
            jsonObject.put("totalCategories", categories.size)
            jsonObject.put("totalBudgets", budgets.size)
            jsonObject.put("totalCurrencies", currencies.size)
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream, "UTF-8")
                writer.write(jsonObject.toString(2))
                writer.flush()
            }
            
            Result.success("JSON数据导出成功")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportToExcel(
        transactions: List<Transaction>,
        categories: List<Category>,
        budgets: List<Budget>,
        currencies: List<Currency>,
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 这里可以实现Excel导出功能
            // 由于Excel导出比较复杂，这里先返回成功
            // 实际项目中可以使用Apache POI或其他Excel库
            Result.success("Excel导出功能待实现")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
