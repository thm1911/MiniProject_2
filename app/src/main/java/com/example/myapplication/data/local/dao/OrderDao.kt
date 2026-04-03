package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.local.entity.OrderDetailEntity
import com.example.myapplication.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

data class OrderItemRow(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

data class PaidOrderRow(
    val orderId: Long,
    val paidAt: Long?,
    val total: Double
)

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetail(orderDetail: OrderDetailEntity): Long

    @Update
    suspend fun updateOrderDetail(orderDetail: OrderDetailEntity)

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status LIMIT 1")
    suspend fun getOrderByUserAndStatus(userId: Long, status: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status ORDER BY paid_at DESC LIMIT 1")
    suspend fun getLatestOrderByUserAndStatus(userId: Long, status: String): OrderEntity?

    @Query("SELECT * FROM order_details WHERE order_id = :orderId AND product_id = :productId LIMIT 1")
    suspend fun getOrderDetail(orderId: Long, productId: Long): OrderDetailEntity?

    @Query(
        """
        SELECT od.product_id AS productId, p.name AS productName, od.quantity AS quantity, od.unit_price AS unitPrice
        FROM order_details od
        INNER JOIN products p ON p.id = od.product_id
        WHERE od.order_id = :orderId
        ORDER BY od.id DESC
        """
    )
    fun getOrderItems(orderId: Long): Flow<List<OrderItemRow>>

    @Query("SELECT COALESCE(SUM(quantity * unit_price), 0) FROM order_details WHERE order_id = :orderId")
    fun getOrderTotal(orderId: Long): Flow<Double>

    @Query("UPDATE orders SET status = :newStatus, paid_at = :paidAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: String, paidAt: Long)

    @Query("DELETE FROM order_details WHERE order_id = :orderId AND product_id = :productId")
    suspend fun deleteOrderDetail(orderId: Long, productId: Long)

    @Query("SELECT paid_at FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getPaidAtByOrderId(orderId: Long): Long?

    @Query(
        """
        SELECT o.id AS orderId,
               o.paid_at AS paidAt,
               COALESCE(SUM(od.quantity * od.unit_price), 0) AS total
        FROM orders o
        LEFT JOIN order_details od ON od.order_id = o.id
        WHERE o.user_id = :userId AND o.status = :status
        GROUP BY o.id
        ORDER BY o.paid_at DESC
        """
    )
    fun getPaidOrders(userId: Long, status: String): Flow<List<PaidOrderRow>>

    @Transaction
    suspend fun addProductToOrder(orderId: Long, productId: Long, unitPrice: Double) {
        val current = getOrderDetail(orderId, productId)
        if (current == null) {
            insertOrderDetail(
                OrderDetailEntity(
                    orderId = orderId,
                    productId = productId,
                    quantity = 1,
                    unitPrice = unitPrice
                )
            )
        } else {
            updateOrderDetail(current.copy(quantity = current.quantity + 1))
        }
    }
}