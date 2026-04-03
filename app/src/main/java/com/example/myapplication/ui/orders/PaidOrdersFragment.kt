package com.example.myapplication.ui.orders

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
import com.example.myapplication.R
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.OrderRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentPaidOrdersBinding
import kotlinx.coroutines.launch


class PaidOrdersFragment : Fragment() {
    private var _binding: FragmentPaidOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val viewModel: PaidOrdersViewModel by viewModels {
        val userId = SessionManager(requireContext()).getCurrentUserId() ?: 0L
        PaidOrdersViewModelFactory(
            OrderRepository(AppDatabase.getInstance(requireContext()).orderDao()),
            userId
        )
    }

    private val adapter = PaidOrdersAdapter { orderId ->
        // Use navigation action to avoid runtime action-resolution issues.
        findNavController().navigate(
            R.id.action_paidOrdersFragment_to_paidOrderDetailFragment,
            android.os.Bundle().apply { putLong("orderId", orderId) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaidOrdersBinding.inflate(inflater, container, false)
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
        binding.rvPaidOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PaidOrdersFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.tvEmptyPaidOrders.visibility =
                        if (state.paidOrders.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(state.paidOrders)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}