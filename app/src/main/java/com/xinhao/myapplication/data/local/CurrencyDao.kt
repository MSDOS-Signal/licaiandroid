package com.xinhao.myapplication.data.local

import androidx.room.*
import com.xinhao.myapplication.data.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    
    @Query("SELECT * FROM currencies ORDER BY sortOrder ASC")
    fun getAllCurrencies(): Flow<List<Currency>>
    
    @Query("SELECT * FROM currencies WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveCurrencies(): Flow<List<Currency>>
    
    @Query("SELECT * FROM currencies WHERE code = :code")
    suspend fun getCurrencyByCode(code: String): Currency?
    
    @Query("SELECT * FROM currencies WHERE isBase = 1 LIMIT 1")
    suspend fun getBaseCurrency(): Currency?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)
    
    @Update
    suspend fun updateCurrency(currency: Currency)
    
    @Delete
    suspend fun deleteCurrency(currency: Currency)
    
    @Query("UPDATE currencies SET isBase = 0 WHERE isBase = 1")
    suspend fun clearBaseCurrency()
    
    @Query("UPDATE currencies SET isBase = 1 WHERE code = :code")
    suspend fun setBaseCurrency(code: String)
    
    @Query("UPDATE currencies SET exchangeRate = :rate WHERE code = :code")
    suspend fun updateExchangeRate(code: String, rate: Double)
}
