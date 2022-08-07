package dev.ogabek.marketwithbarcode.viewmodel.scan

import dev.ogabek.marketwithbarcode.db.ProductDao
import dev.ogabek.marketwithbarcode.model.Product
import javax.inject.Inject

class ScanRepository @Inject constructor(private val dao: ProductDao) {

    suspend fun getAllProducts() = dao.getAllProducts()
    suspend fun getProduct(barCode: Long) = dao.getSingleProduct(barCode)

}