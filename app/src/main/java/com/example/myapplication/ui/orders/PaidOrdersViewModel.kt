package com.example.myapplication.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.dao.PaidOrderRow
import com.example.myapplication.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaidOrdersUiState(
    val isLoading: Boolean = true,
    val paidOrders: List<PaidOrderRow> = emptyList()
)

class PaidOrdersViewModel(
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaidOrdersUiState())
    val uiState: StateFlow<PaidOrdersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            orderRepository.getPaidOrders(userId).collect { paidOrders ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    paidOrders = paidOrders
                )
            }
        }
    }
}

class PaidOrdersViewModelFactory(
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaidOrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaidOrdersViewModel(orderRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}