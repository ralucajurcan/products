package com.example.products.presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.products.R
import com.example.products.common.model.Product

class ProductsAdapter(
    private var products: List<Product>,
    private val onNoteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productText: TextView = itemView.findViewById(R.id.productText)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val note = products[position]
        holder.productText.text = note.title

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(note.imageUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.productImage)

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
