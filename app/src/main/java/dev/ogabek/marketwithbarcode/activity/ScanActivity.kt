package dev.ogabek.marketwithbarcode.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import dagger.hilt.android.AndroidEntryPoint
import dev.ogabek.marketwithbarcode.databinding.ActivityScanBinding
import dev.ogabek.marketwithbarcode.model.Product
import dev.ogabek.marketwithbarcode.utils.UiStateList
import dev.ogabek.marketwithbarcode.utils.UiStateObject
import dev.ogabek.marketwithbarcode.viewmodel.scan.ScanViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScanActivity : AppCompatActivity() {

    private val viewModel: ScanViewModel by viewModels()

    private var barCode: Long = 0

    private lateinit var binding: ActivityScanBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101);

        setUpObservers()
        initViews()

    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewModel.isHave.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading(this@ScanActivity)
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        chooseWay(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        showError(this@ScanActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.product.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading(this@ScanActivity)
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        val intent = Intent(this@ScanActivity, DetailsActivity::class.java)
                        intent.putExtra("product", it.data)
                        startActivity(intent)
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        showError(this@ScanActivity, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun chooseWay(data: List<Product>) {
        var isHave = false
        for (i in data) {
            if (i.barCode == barCode){
                isHave = true
                break
            }
        }

        if (isHave) {
            viewModel.getProduct(barCode)
        } else {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra("barcode", barCode)
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        codeScanner = CodeScanner(this, binding.vScanner)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                barCode = it.text!!.toLong()
                if (intent.getBooleanExtra("isForCode", false)) {
                    intent.putExtra("barcode", barCode.toString())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    viewModel.isHave()
                }

            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.vScanner.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
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