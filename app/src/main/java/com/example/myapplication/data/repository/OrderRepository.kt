package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.OrderDao
import com.example.myapplication.data.local.dao.OrderItemRow
import com.example.myapplication.data.local.dao.PaidOrderRow
import com.example.myapplication.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao
) {
    suspend fun addProductToCart(userId: Long, productId: Long, unitPrice: Double) {
        val pendingOrder = orderDao.getOrderByUserAndStatus(userId, OrderEntity.STATUS_PENDING)
            ?: createPendingOrder(userId)
        orderDao.addProductToOrder(pendingOrder.id, productId, unitPrice)
    }

    suspend fun getPendingOrder(userId: Long): OrderEntity? {
        return orderDao.getOrderByUserAndStatus(userId, OrderEntity.STATUS_PENDING)
    }

    suspend fun getLatestPaidOrder(userId: Long): OrderEntity? {
        return orderDao.getLatestOrderByUserAndStatus(userId, OrderEntity.STATUS_PAID)
    }

    fun getOrderItems(orderId: Long): Flow<List<OrderItemRow>> = orderDao.getOrderItems(orderId)

    fun getOrderTotal(orderId: Long): Flow<Double> = orderDao.getOrderTotal(orderId)

    suspend fun checkout(orderId: Long) {
        orderDao.updateOrderStatus(orderId, OrderEntity.STATUS_PAID, System.currentTimeMillis())
    }

    suspend fun removeProductFromPendingOrder(userId: Long, productId: Long) {
        val pendingOrder = orderDao.getOrderByUserAndStatus(userId, OrderEntity.STATUS_PENDING) ?: return
        orderDao.deleteOrderDetail(pendingOrder.id, productId)
    }

    fun getPaidOrders(userId: Long): Flow<List<PaidOrderRow>> =
        orderDao.getPaidOrders(userId, OrderEntity.STATUS_PAID)

    suspend fun getPaidAt(orderId: Long): Long? = orderDao.getPaidAtByOrderId(orderId)

    private suspend fun createPendingOrder(userId: Long): OrderEntity {
        val orderId = orderDao.insertOrder(
            OrderEntity(userId = userId, status = OrderEntity.STATUS_PENDING)
        )
        return OrderEntity(id = orderId, userId = userId, status = OrderEntity.STATUS_PENDING)
    }
}