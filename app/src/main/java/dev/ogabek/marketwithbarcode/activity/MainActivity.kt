package dev.ogabek.marketwithbarcode.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.adapter.ProductAdapter
import dev.ogabek.marketwithbarcode.databinding.ActivityMainBinding
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateList
import dev.ogabek.marketwithbarcode.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val adapter by lazy { ProductAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAppProducts()
        setUpObservers()
        initViews()

        binding.rvProducts.adapter = adapter
        adapter.onItemClick = {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("product", it)
            startActivity(intent)
        }

    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewModel.products.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading(this@MainActivity)
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        refreshAdapter(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        showError(this@MainActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun refreshAdapter(data: List<Product>) {
        (binding.rvProducts.adapter as ProductAdapter).updateData(data)
    }

    private fun initViews() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

        binding.btnStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

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
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Error")
        alertDialog.setMessage(text)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Exit App") { _, _ ->
            finish()
        }

        alertDialog.show()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.getAppProducts()
    }

}