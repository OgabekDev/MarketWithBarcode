package dev.ogabek.marketwithbarcode.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val barCode: Long,
    val name: String,
    var quantity: Int,
    val price: Double,
    val priceForSale: Double,
    val photo: String
): Serializable