package com.xinhao.myapplication.data.local

import androidx.room.*
import com.xinhao.myapplication.data.model.TransactionImage
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionImageDao {
    
    @Query("SELECT * FROM transaction_images WHERE transactionId = :transactionId")
    fun getImagesByTransactionId(transactionId: Long): Flow<List<TransactionImage>>
    
    @Query("SELECT * FROM transaction_images WHERE id = :imageId")
    suspend fun getImageById(imageId: Long): TransactionImage?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: TransactionImage): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<TransactionImage>)
    
    @Update
    suspend fun updateImage(image: TransactionImage)
    
    @Delete
    suspend fun deleteImage(image: TransactionImage)
    
    @Query("DELETE FROM transaction_images WHERE transactionId = :transactionId")
    suspend fun deleteImagesByTransactionId(transactionId: Long)
    
    @Query("DELETE FROM transaction_images WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Long)
    
    @Query("SELECT COUNT(*) FROM transaction_images WHERE transactionId = :transactionId")
    suspend fun getImageCountByTransactionId(transactionId: Long): Int
}
