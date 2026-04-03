package com.example.myapplication.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.dao.OrderItemRow
import com.example.myapplication.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PaidOrderDetailUiState(
    val isLoading: Boolean = true,
    val orderId: Long? = null,
    val paidAt: Long? = null,
    val paidAtText: String? = null,
    val items: List<OrderItemRow> = emptyList(),
    val total: Double = 0.0
)

class PaidOrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val orderId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaidOrderDetailUiState(orderId = orderId))
    val uiState: StateFlow<PaidOrderDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val paidAt = orderRepository.getPaidAt(orderId)
            val paidAtText = paidAt?.let { formatPaidAt(it) }
            _uiState.value = _uiState.value.copy(
                paidAt = paidAt,
                paidAtText = paidAtText
            )
        }

        viewModelScope.launch {
            combine(
                orderRepository.getOrderItems(orderId),
                orderRepository.getOrderTotal(orderId)
            ) { items, total -> items to total }
                .collect { (items, total) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = items,
                        total = total
                    )
                }
        }
    }

    private fun formatPaidAt(paidAt: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(paidAt))
    }
}

class PaidOrderDetailViewModelFactory(
    private val orderRepository: OrderRepository,
    private val orderId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaidOrderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaidOrderDetailViewModel(orderRepository, orderId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}