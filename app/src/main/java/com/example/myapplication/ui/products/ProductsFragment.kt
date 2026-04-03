package com.example.myapplication.ui.products
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.OrderRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var orderRepository: OrderRepository
    private val args: ProductsFragmentArgs by navArgs()
    private val productAdapter = ProductAdapter(
        onItemClick = { product ->
            val action = ProductsFragmentDirections.actionProductsFragmentToProductDetailFragment(
                product.id
            )
            findNavController().navigate(action)
        },
        onAddToCartClick = { product ->
            addToCart(product.id, product.price)
        }
    )
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(
            ProductRepository(
                AppDatabase.getInstance(requireContext()).productDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        orderRepository = OrderRepository(AppDatabase.getInstance(requireContext()).orderDao())
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvProductsTitle.text = if (args.categoryTitle.isNotBlank()) {
            args.categoryTitle
        } else {
            getString(R.string.products_screen_title)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    productAdapter.submitList(state.products)
                    binding.tvEmptyProducts.visibility =
                        if (state.products.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addToCart(productId: Long, unitPrice: Double) {
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(requireContext(), R.string.require_login_message, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            orderRepository.addProductToCart(userId, productId, unitPrice)
            Toast.makeText(requireContext(), R.string.added_to_cart_success, Toast.LENGTH_SHORT).show()
        }
    }
}