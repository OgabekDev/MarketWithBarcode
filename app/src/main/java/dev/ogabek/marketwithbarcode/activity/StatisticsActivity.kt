package dev.ogabek.marketwithbarcode.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.databinding.ActivityStatisticsBinding
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateList
import dev.ogabek.marketwithbarcode.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAppProducts()

        setUpObservers()

    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewModel.products.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading(this@StatisticsActivity)
                    }
                    is UiStateList.SUCCESS -> {
                        setUpData(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        showError(this@StatisticsActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpData(data: List<Product>) {
        binding.tvQuantity.text = "All Products : ${setAllProducts(data)}"
        binding.tvPriceAll.text = "Price All Products : ${setAllPrices(data)} UZS"
        binding.tvPriceForSaleAll.text = "Price All Products For Sale : ${setAllPricesForSale(data)} UZS"
        binding.tvIncomeForAll.text = "Price All Products For Sale : ${setAllPricesIncome(data)} UZS"
        dismissLoading()
    }

    private fun setAllPrices(data: List<Product>): Double {
        var sum = 0.0
        for (i in data) {
            sum += i.price
        }
        return sum
    }

    private fun setAllPricesForSale(data: List<Product>): Double {
        var sum = 0.0
        for (i in data) {
            sum += i.priceForSale
        }
        return sum
    }

    private fun setAllPricesIncome(data: List<Product>): Double {
        var sum = 0.0
        for (i in data) {
            sum += (i.priceForSale - i.price)
        }
        return sum
    }

    private fun setAllProducts(data: List<Product>): Int {
        var sum: Int = 0
        for (i in data) {
            sum += i.quantity
        }
        return sum
    }

    private lateinit var dialog: ProgressDialog

    private fun showLoading(context: Context) {
        dialog = ProgressDialog(context)
        dialog.setCancelable(false)
        dialog.setMessage("Please wait")
        dialog.show()
    }

    private fun dismissLoading() {
        dialog.dismiss()
    }

    private fun showError(context: Context, text: String) {
        val alertDialog = android.app.AlertDialog.Builder(context)
        alertDialog.setTitle("Error")
        alertDialog.setMessage(text)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Exit App") { _, _ ->
            finish()
        }

        alertDialog.show()
    }

}