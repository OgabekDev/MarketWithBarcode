package dev.ogabek.marketwithbarcode.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.databinding.ActivityDetailsBinding
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.DialogAlert
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import dev.ogabek.marketwithbarcode.viewmodel.details.DetailsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpObserver()

    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            viewModel.change.collect {
                when(it) {
                    is UiStateObject.LOADING -> showLoading(this@DetailsActivity)
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        setData(product)
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        showError(this@DetailsActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initViews() {

        product = intent.getSerializableExtra("product")!! as Product

        setData(product)

        binding.btnAdd.setOnClickListener {
            showAddDialog(product)
        }

        binding.btnSell.setOnClickListener {
            showSellDialog(product)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setData(product: Product) {
        Glide.with(binding.ivProduct).load(Uri.parse(product.photo)).placeholder(R.drawable.no_photo).into(binding.ivProduct)

        binding.tvName.text = product.name
        binding.tvQuantity.text = "Quantity : ${product.quantity}"

        binding.tvBarcode.text = "Barcode : ${product.barCode}"

        binding.tvPrice.text = "Price : ${product.price} UZS"
        binding.tvPriceForSale.text = "Price For Sale : ${product.priceForSale} UZS"

        binding.tvPriceAll.text = "Price All Products : ${product.price * product.quantity} UZS"
        binding.tvPriceForSaleAll.text = "Price All Products For Sale : ${product.priceForSale * product.quantity} UZS"

        binding.tvIncome.text = "Income Per Product : ${product.priceForSale - product.price} UZS"
        binding.tvIncomeForAll.text = "Income All Product : ${(product.priceForSale - product.price) * product.quantity} UZS"
    }


    private fun showSellDialog(product: Product) {
        val dialog = DialogAlert(product, true)
        dialog.clickListener = {
            viewModel.change(product.barCode, product.quantity - it)
            product.quantity -= it
        }

        dialog.show(supportFragmentManager, "sell")

    }

    private fun showAddDialog(product: Product) {
        val dialog = DialogAlert(product, false)
        dialog.clickListener = {
            viewModel.change(product.barCode, product.quantity + it)
            product.quantity += it
        }

        dialog.show(supportFragmentManager, "add")

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
