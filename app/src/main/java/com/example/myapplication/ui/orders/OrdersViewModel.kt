package com.example.myapplication.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.dao.OrderItemRow
import com.example.myapplication.data.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrdersUiState(
    val isLoading: Boolean = true,
    val pendingOrderId: Long? = null,
    val pendingItems: List<OrderItemRow> = emptyList(),
    val pendingTotal: Double = 0.0,
    val invoiceMessage: String? = null,
    val statusMessage: String? = null
)

class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    private var pendingOrderJob: Job? = null

    init {
        refreshOrder()
    }

    fun refreshOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            val pendingOrder = orderRepository.getPendingOrder(userId)
            pendingOrderJob?.cancel()

            if (pendingOrder != null) {
                pendingOrderJob = launch {
                    combine(
                        orderRepository.getOrderItems(pendingOrder.id),
                        orderRepository.getOrderTotal(pendingOrder.id)
                    ) { items, total -> items to total }
                        .collect { (items, total) ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                pendingOrderId = pendingOrder.id,
                                pendingItems = items,
                                pendingTotal = total
                            )
                        }
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingOrderId = null,
                    pendingItems = emptyList(),
                    pendingTotal = 0.0
                )
            }

            val paidOrder = orderRepository.getLatestPaidOrder(userId)
            if (paidOrder != null) {
                val paidTotal = orderRepository.getOrderTotal(paidOrder.id).first()
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val paidTime = paidOrder.paidAt?.let { formatter.format(Date(it)) }.orEmpty()
                val invoice =
                    "Hóa đơn #${paidOrder.id} - Tổng: ${formatMoney(paidTotal)} - Đã thanh toán lúc $paidTime"
                _uiState.value = _uiState.value.copy(invoiceMessage = invoice)
            }
        }
    }

    fun checkout() {
        val orderId = _uiState.value.pendingOrderId ?: return
        viewModelScope.launch {
            orderRepository.checkout(orderId)
            _uiState.value = _uiState.value.copy(statusMessage = "Thanh toán thành công")
            refreshOrder()
        }
    }

    fun removeProductFromPending(productId: Long) {
        viewModelScope.launch {
            orderRepository.removeProductFromPendingOrder(userId, productId)
            // No need to manually refresh: the pending order items/total are driven by Flows.
        }
    }

    fun consumeStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    private fun formatMoney(value: Double): String = String.format(Locale.getDefault(), "%.0f VND", value)
}

class OrdersViewModelFactory(
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdersViewModel(orderRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}