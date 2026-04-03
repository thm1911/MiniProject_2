package com.example.myapplication.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.local.entity.ProductEntity
import com.example.myapplication.databinding.ItemProductBinding

class ProductAdapter(
    private val onItemClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onItemClick: (ProductEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            binding.root.setOnClickListener { onItemClick(item) }
            Glide.with(binding.ivProduct.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(binding.ivProduct)
            binding.tvName.text = item.name
            binding.tvDescription.text = item.description
            binding.tvPrice.text = binding.root.context.getString(
                R.string.product_price_format,
                item.price
            )
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}