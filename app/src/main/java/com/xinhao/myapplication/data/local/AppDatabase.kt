package com.xinhao.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xinhao.myapplication.data.model.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(
    entities = [
        Transaction::class, 
        TransactionImage::class,
        Category::class, 
        Budget::class, 
        Currency::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionImageDao(): TransactionImageDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 在数据库创建时插入默认数据
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = INSTANCE
                            if (database != null) {
                                insertDefaultData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private suspend fun insertDefaultData(database: AppDatabase) {
            val now = LocalDateTime.now()
            
            // 插入默认分类
            DefaultCategories.expenseCategories.forEach { category ->
                val categoryWithTime = category.copy(createdAt = now, updatedAt = now)
                database.categoryDao().insertCategory(categoryWithTime)
            }
            DefaultCategories.incomeCategories.forEach { category ->
                val categoryWithTime = category.copy(createdAt = now, updatedAt = now)
                database.categoryDao().insertCategory(categoryWithTime)
            }
            
            // 插入默认货币
            DefaultCurrencies.currencies.forEach { currency ->
                val currencyWithTime = currency.copy(createdAt = now, updatedAt = now)
                database.currencyDao().insertCurrency(currencyWithTime)
            }
        }
    }
}
