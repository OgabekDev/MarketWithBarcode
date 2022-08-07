package dev.ogabek.marketwithbarcode.viewmodel.create

import dev.ogabek.marketwithbarcode.db.ProductDao
import dev.ogabek.marketwithbarcode.model.Product
import javax.inject.Inject

class CreateRepository @Inject constructor(private val dao: ProductDao) {

    suspend fun createProduct(product: Product) = dao.addProduct(product)

}