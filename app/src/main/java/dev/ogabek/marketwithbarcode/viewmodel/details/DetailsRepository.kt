package dev.ogabek.marketwithbarcode.viewmodel.details

import dev.ogabek.marketwithbarcode.db.ProductDao
import javax.inject.Inject

class DetailsRepository @Inject constructor(private val dao: ProductDao) {

    suspend fun change(barcode: Long, quantity: Int) = dao.changeProductQuantity(barcode, quantity)

}