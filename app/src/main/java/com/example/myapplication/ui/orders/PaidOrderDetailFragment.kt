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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.OrderRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentPaidOrderDetailBinding
import kotlinx.coroutines.launch

class PaidOrderDetailFragment : Fragment() {
    private var _binding: FragmentPaidOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PaidOrderDetailFragmentArgs by navArgs()
    private val viewModel: PaidOrderDetailViewModel by viewModels {
        val orderId = args.orderId
        PaidOrderDetailViewModelFactory(
            OrderRepository(AppDatabase.getInstance(requireContext()).orderDao()),
            orderId
        )
    }

    private val adapter = PaidOrderItemsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaidOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!SessionManager(requireContext()).isLoggedIn()) {
            findNavController().navigate(R.id.loginFragment)
            return
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.rvPaidOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PaidOrderDetailFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.tvPaidAt.text = if (state.paidAtText.isNullOrBlank()) {
                        getString(R.string.paid_at_label)
                    } else {
                        "${getString(R.string.paid_at_label)}: ${state.paidAtText}"
                    }
                    binding.tvOrderTotalDetail.text = getString(
                        R.string.order_total_format,
                        state.total
                    )
                    adapter.submitList(state.items)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}