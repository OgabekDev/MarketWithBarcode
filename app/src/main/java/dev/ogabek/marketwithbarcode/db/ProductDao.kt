package dev.ogabek.marketwithbarcode.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.ogabek.marketwithbarcode.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE barCode = :barCode")
    suspend fun getSingleProduct(barCode: Long): Product

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: Product)

    @Query("UPDATE products SET quantity = :quantity WHERE barCode = :barCode")
    suspend fun changeProductQuantity(barCode: Long, quantity: Int)

    @Query("DELETE FROM products WHERE barCode = :barCode")
    suspend fun deleteProduct(barCode: Long)

}