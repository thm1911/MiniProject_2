package com.example.myapplication.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.local.entity.ProductEntity
import com.example.myapplication.databinding.ItemCategorySectionBinding

class CategoriesSectionAdapter(
    private val onProductClick: (CategorySectionUi, ProductEntity) -> Unit,
    private val onViewAllClick: (CategorySectionUi) -> Unit
) : ListAdapter<CategorySectionUi, CategoriesSectionAdapter.SectionVH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionVH {
        val binding = ItemCategorySectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SectionVH(binding, onProductClick, onViewAllClick)
    }

    override fun onBindViewHolder(holder: SectionVH, position: Int) {
        holder.bind(getItem(position))
    }

    class SectionVH(
        private val binding: ItemCategorySectionBinding,
        private val onProductClick: (CategorySectionUi, ProductEntity) -> Unit,
        private val onViewAllClick: (CategorySectionUi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var boundSection: CategorySectionUi? = null
        private val previewAdapter = ProductPreviewAdapter { product ->
            boundSection?.let { onProductClick(it, product) }
        }

        init {
            binding.rvPreviewProducts.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rvPreviewProducts.adapter = previewAdapter
        }

        fun bind(section: CategorySectionUi) {
            boundSection = section
            binding.tvCategoryName.text = section.category.name
            binding.btnViewAll.setOnClickListener { onViewAllClick(section) }
            previewAdapter.submitList(section.previewProducts)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CategorySectionUi>() {
            override fun areItemsTheSame(a: CategorySectionUi, b: CategorySectionUi): Boolean {
                return a.category.id == b.category.id
            }

            override fun areContentsTheSame(a: CategorySectionUi, b: CategorySectionUi): Boolean {
                return a.category == b.category && a.previewProducts == b.previewProducts
            }
        }
    }
}