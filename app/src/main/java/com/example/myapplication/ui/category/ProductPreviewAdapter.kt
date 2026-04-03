package com.example.myapplication.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.local.entity.ProductEntity
import com.example.myapplication.databinding.ItemProductPreviewBinding
import com.example.myapplication.util.formatProductExpiry

class ProductPreviewAdapter(
    private val onProductClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductPreviewAdapter.PreviewVH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewVH {
        val binding = ItemProductPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PreviewVH(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: PreviewVH, position: Int) {
        holder.bind(getItem(position))
    }

    class PreviewVH(
        private val binding: ItemProductPreviewBinding,
        private val onProductClick: (ProductEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            binding.root.setOnClickListener { onProductClick(item) }
            Glide.with(binding.ivProduct.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(binding.ivProduct)
            binding.tvName.text = item.name
            binding.tvPrice.text = binding.root.context.getString(
                R.string.product_price_format,
                item.price
            )
            binding.tvExpiry.text = formatProductExpiry(binding.root.context, item.expiryDateMillis)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(a: ProductEntity, b: ProductEntity): Boolean = a.id == b.id
            override fun areContentsTheSame(a: ProductEntity, b: ProductEntity): Boolean = a == b
        }
    }
}