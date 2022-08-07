package dev.ogabek.marketwithbarcode.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.databinding.DialogSellBinding
import dev.ogabek.marketwithbarcode.model.Product

class DialogAlert(private val product: Product, private val forSell: Boolean) : DialogFragment() {
    lateinit var clickListener: ((Int) -> Unit)
    private var _bn: DialogSellBinding? = null
    private val binding get() = _bn!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bn = DialogSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        binding.apply {
            btnSell.setOnClickListener {
                clickListener.invoke(etAmount.text.toString().toInt())
                dismiss()
            }

            if (forSell) {
                tvAmount.visibility = View.VISIBLE
            } else {
                tvAmount.visibility = View.GONE
            }

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(p0: Editable?) {
                    if (!p0.isNullOrEmpty()) {
                        if (forSell) {
                            val quantity: Long = p0.toString().toLong()
                            if (quantity > product.quantity) {
                                tvAmount.text = "You cannot sell more than you have"
                                binding.btnSell.isEnabled = false
                            } else {
                                tvAmount.text = "Amount : ${quantity * product.priceForSale} UZS"
                                binding.btnSell.isEnabled = true
                            }
                        }
                    } else {
                        tvAmount.text = "Amount : 0 UZS"
                    }
                }

            })

            btnCancel.setOnClickListener {
                dismiss()
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}