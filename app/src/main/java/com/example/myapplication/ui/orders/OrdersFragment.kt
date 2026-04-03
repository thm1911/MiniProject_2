package com.example.myapplication.ui.orders

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.OrderRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentOrdersBinding
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private val viewModel: OrdersViewModel by viewModels {
        val userId = SessionManager(requireContext()).getCurrentUserId() ?: 0L
        OrdersViewModelFactory(
            OrderRepository(
                AppDatabase.getInstance(requireContext()).orderDao()),
            userId
        )
    }

    private val orderItemsAdapter = PendingOrderItemsAdapter { productId ->
        showRemoveConfirmDialog(productId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        if (!sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.loginFragment)
            return
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnCheckout.setOnClickListener { viewModel.checkout() }
        binding.btnViewPaidOrders.setOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_paidOrdersFragment)
        }

        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemsAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    val hasPendingItems = state.pendingItems.isNotEmpty()
                    val hasPaidOrders = !state.invoiceMessage.isNullOrBlank()
                    binding.layoutOrderContent.visibility = if (hasPendingItems || hasPaidOrders) View.VISIBLE else View.GONE
                    binding.tvEmptyOrder.visibility = if (!hasPendingItems && !hasPaidOrders) View.VISIBLE else View.GONE

                    binding.rvOrderItems.visibility = if (hasPendingItems) View.VISIBLE else View.GONE
                    binding.tvOrderTotal.visibility = if (hasPendingItems) View.VISIBLE else View.GONE
                    binding.btnCheckout.visibility = if (hasPendingItems) View.VISIBLE else View.GONE

                    orderItemsAdapter.submitList(state.pendingItems)
                    if (hasPendingItems) {
                        binding.tvOrderTotal.text = getString(
                            R.string.order_total_format,
                            state.pendingTotal
                        )
                    }
                    if (!state.statusMessage.isNullOrBlank()) {
                        Toast.makeText(requireContext(), state.statusMessage, Toast.LENGTH_SHORT).show()
                        viewModel.consumeStatusMessage()
                    }
                }
            }
        }
    }

    private fun showRemoveConfirmDialog(productId: Long) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.remove_product_from_order_confirm_title)
            .setMessage(R.string.remove_product_from_order_confirm_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeProductFromPending(productId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}