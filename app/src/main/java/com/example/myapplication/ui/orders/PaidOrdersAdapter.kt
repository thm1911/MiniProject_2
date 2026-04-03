package com.example.myapplication.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.local.dao.PaidOrderRow
import com.example.myapplication.databinding.ItemPaidOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaidOrdersAdapter(
    private val onOrderClick: (orderId: Long) -> Unit
) : ListAdapter<PaidOrderRow, PaidOrdersAdapter.PaidOrderVH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaidOrderVH {
        val binding = ItemPaidOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaidOrderVH(binding, onOrderClick)
    }

    override fun onBindViewHolder(holder: PaidOrderVH, position: Int) {
        holder.bind(getItem(position))
    }

    class PaidOrderVH(
        private val binding: ItemPaidOrderBinding,
        private val onOrderClick: (orderId: Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(item: PaidOrderRow) {
            binding.tvPaidOrderId.text = "Hóa đơn #${item.orderId}"
            val paidAtText = item.paidAt?.let { formatter.format(Date(it)) }.orEmpty()
            binding.tvPaidAt.text = "$paidAtText"
            binding.tvPaidOrderTotal.text = binding.root.context.getString(
                R.string.order_total_format,
                item.total
            )
            binding.root.setOnClickListener { onOrderClick(item.orderId) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PaidOrderRow>() {
            override fun areItemsTheSame(oldItem: PaidOrderRow, newItem: PaidOrderRow): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(oldItem: PaidOrderRow, newItem: PaidOrderRow): Boolean {
                return oldItem == newItem
            }
        }
    }
}