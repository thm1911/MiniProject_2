package com.example.myapplication.ui.productdetail

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
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.OrderRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentProductDetailBinding
import com.example.myapplication.util.formatProductExpiry
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var orderRepository: OrderRepository

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()

    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(
            ProductRepository(
                AppDatabase.getInstance(requireContext()).productDao()
            ),
            args.productId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        orderRepository = OrderRepository(AppDatabase.getInstance(requireContext()).orderDao())
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnAddToCart.setOnClickListener {
            addCurrentProductToCart()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val product = state.product
                    if (product != null) {
                        binding.scrollContent.visibility = View.VISIBLE
                        binding.tvProductDetailTitle.text = product.name
                        Glide.with(binding.ivProductDetail)
                            .load(product.imageUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .centerCrop()
                            .into(binding.ivProductDetail)
                        binding.tvName.text = product.name
                        binding.tvDescription.text = product.description
                        binding.tvPrice.text =
                            getString(R.string.product_price_format, product.price)
                        binding.tvExpiry.text = formatProductExpiry(requireContext(), product.expiryDateMillis)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addCurrentProductToCart() {
        val product = viewModel.uiState.value.product ?: return
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(requireContext(), R.string.require_login_message, Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.loginFragment)
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            orderRepository.addProductToCart(userId, product.id, product.price)
            Toast.makeText(requireContext(), R.string.added_to_cart_success, Toast.LENGTH_SHORT)
                .show()
        }
    }
}
