package dev.ogabek.marketwithbarcode.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ogabek.marketwithbarcode.R
import dev.ogabek.marketwithbarcode.databinding.ItemProductBinding
import dev.ogabek.marketwithbarcode.model.Product

class ProductAdapter(private val items: List<Product>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ItemViewHolder(view)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemProductBinding.bind(view)
    }

    fun updateData(newList: List<Product>) {
        (items as ArrayList).clear()
        items.addAll(newList as ArrayList)
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = items[position]
        if (holder is ItemViewHolder) {
            holder.binding.apply {
                Glide.with(ivProduct).load(product.photo).placeholder(R.drawable.no_photo)
                    .into(ivProduct)
                tvName.text = product.name
                tvQuantity.text = product.quantity.toString()
                tvPrice.text = "${product.priceForSale} UZS"

                root.setOnClickListener {
                    onItemClick!!.invoke(product)
                }
            }
        }
    }

    override fun getItemCount() = items.size
}