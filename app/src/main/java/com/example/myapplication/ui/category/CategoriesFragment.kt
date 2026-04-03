package com.example.myapplication.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.CategoryRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.databinding.FragmentCategoriesBinding
import com.example.myapplication.ui.categories.CategoriesFragmentDirections
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val sectionAdapter = CategoriesSectionAdapter(
        onProductClick = { _, product ->
            val action = CategoriesFragmentDirections
                .actionCategoriesFragmentToProductDetailFragment(product.id)
            findNavController().navigate(action)
        },
        onViewAllClick = { section ->
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToProductsFragment(
                section.category.id,
                section.category.name
            )
            findNavController().navigate(action)
        }
    )

    private val viewModel: CategoriesViewModel by viewModels {
        CategoriesViewModelFactory(
            CategoryRepository(AppDatabase.getInstance(requireContext()).categoryDao()),
            ProductRepository(AppDatabase.getInstance(requireContext()).productDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rvCategorySections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sectionAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    sectionAdapter.submitList(state.sections)
                    binding.tvEmptyCategories.visibility =
                        if (state.sections.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}