package com.example.myapplication.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.local.dao.OrderItemRow
import com.example.myapplication.databinding.ItemOrderProductBinding

class PaidOrderItemsAdapter : ListAdapter<OrderItemRow, PaidOrderItemsAdapter.ItemVH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemVH(binding)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemVH(
        private val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItemRow) {
            binding.btnRemove.visibility = View.GONE
            binding.tvProductName.text = item.productName
            binding.tvProductMeta.text = "Số lượng: ${item.quantity}"
            val lineTotal = item.quantity * item.unitPrice
            binding.tvLineTotal.text = binding.root.context.getString(
                R.string.product_price_format,
                lineTotal
            )
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<OrderItemRow>() {
            override fun areItemsTheSame(oldItem: OrderItemRow, newItem: OrderItemRow): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(oldItem: OrderItemRow, newItem: OrderItemRow): Boolean {
                return oldItem == newItem
            }
        }
    }
}