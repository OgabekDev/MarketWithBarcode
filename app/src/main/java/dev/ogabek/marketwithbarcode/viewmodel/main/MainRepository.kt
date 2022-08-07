package dev.ogabek.marketwithbarcode.viewmodel.main

import dev.ogabek.marketwithbarcode.db.ProductDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val dao: ProductDao) {

    suspend fun getAllList() = dao.getAllProducts()

}