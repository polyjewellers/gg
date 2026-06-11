package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    // --- CUSTOMERS ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Long): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE mobile = :mobile LIMIT 1")
    suspend fun getCustomerByMobile(mobile: String): CustomerEntity?

    // --- ORDERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Query("""
        SELECT orders.*, customers.name as customerName, customers.mobile as customerMobile 
        FROM orders 
        INNER JOIN customers ON orders.customerId = customers.id
        ORDER BY orders.orderDate DESC
    """)
    fun getOrdersWithCustomers(): Flow<List<OrderWithCustomer>>

    // --- PAYMENTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY advanceDate DESC")
    fun getPaymentsForOrder(orderId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY advanceDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    // --- DELIVERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity): Long

    @Update
    suspend fun updateDelivery(delivery: DeliveryEntity)

    @Query("SELECT * FROM deliveries WHERE orderId = :orderId ORDER BY deliveryDate DESC")
    fun getDeliveriesForOrder(orderId: Long): Flow<List<DeliveryEntity>>

    @Query("SELECT * FROM deliveries ORDER BY deliveryDate DESC")
    fun getAllDeliveries(): Flow<List<DeliveryEntity>>

    // --- RATES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoldRate(rate: GoldRateEntity): Long

    @Query("SELECT * FROM gold_rates ORDER BY rateDate DESC LIMIT 1")
    fun getLatestGoldRate(): Flow<GoldRateEntity?>

    @Query("SELECT * FROM gold_rates ORDER BY rateDate DESC")
    fun getAllGoldRates(): Flow<List<GoldRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSilverRate(rate: SilverRateEntity): Long

    @Query("SELECT * FROM silver_rates ORDER BY rateDate DESC LIMIT 1")
    fun getLatestSilverRate(): Flow<SilverRateEntity?>

    @Query("SELECT * FROM silver_rates ORDER BY rateDate DESC")
    fun getAllSilverRates(): Flow<List<SilverRateEntity>>

    // --- NOTES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderNote(note: OrderNoteEntity): Long

    @Query("SELECT * FROM order_notes WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getNotesForOrder(orderId: Long): Flow<List<OrderNoteEntity>>

    // --- USERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String): UserEntity?
}

data class OrderWithCustomer(
    @Embedded val order: OrderEntity,
    val customerName: String,
    val customerMobile: String
)
