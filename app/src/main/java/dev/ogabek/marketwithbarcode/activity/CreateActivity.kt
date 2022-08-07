package dev.ogabek.marketwithbarcode.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.databinding.ActivityCreateBinding
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import dev.ogabek.marketwithbarcode.viewmodel.create.CreateViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding
    private val viewModel: CreateViewModel by viewModels()

    private var allPhotos = ArrayList<Uri>()
    private var pickedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpObservers()
        initViews()

    }

    private fun initViews() {

        val barcode = intent.getLongExtra("barcode", 0L).toString()

        if (barcode != "0") {
            binding.etBarCode.setText(barcode)
            binding.etBarCode.isEnabled = false
        } else {
            binding.etBarCode.isEnabled = true
            binding.etBarCode.setText("")
        }

        binding.apply {
            btnSave.setOnClickListener {
                if (etName.text.isNotEmpty() && etPrice.text.isNotEmpty() && etBarCode.text.isNotEmpty() && etPriceForSale.text.isNotEmpty() && etQuantity.text.isNotEmpty()) {
                    val product = Product(
                        etBarCode.text.toString().toLong(),
                        etName.text.toString(),
                        etQuantity.text.toString().toInt(),
                        etPrice.text.toString().toDouble(),
                        etPriceForSale.text.toString().toDouble(),
                        pickedPhoto.toString()
                    )
                    viewModel.create(product)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please fill the all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            ivBarcodeScanner.setOnClickListener {
                val intent = Intent(this@CreateActivity, ScanActivity::class.java)
                intent.putExtra("isForCode", true)
                resultActivity.launch(intent)
            }

            ivProduct.setOnClickListener {
                pickPhoto()
            }

        }

    }

    private var resultActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val barcode = data!!.getStringExtra("barcode")
            if (barcode != "0") {
                binding.etBarCode.setText(barcode)
                binding.etBarCode.isEnabled = false
            } else {
                binding.etBarCode.isEnabled = true
                binding.etBarCode.setText("")
            }
        }
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewModel.create.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading(this@CreateActivity)
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        Toast.makeText(this@CreateActivity, "Done", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        showError(this@CreateActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun pickPhoto() {

        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .setMinCount(1)
            .hasCameraInPickerPage(true)
            .setActionBarColor(R.color.teal_700)
            .setSelectedImages(allPhotos)
            .startAlbumWithActivityResultCallback(photoLauncher)
    }

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                allPhotos =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                pickedPhoto = allPhotos[0]
                Glide.with(binding.ivProduct).load(pickedPhoto).into(binding.ivProduct)
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

}