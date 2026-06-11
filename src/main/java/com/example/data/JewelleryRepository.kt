package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JewelleryRepository(private val dao: DatabaseDao) {

    // Reactive streams
    val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    val allOrders: Flow<List<OrderWithCustomer>> = dao.getOrdersWithCustomers()
    val latestGoldRate: Flow<GoldRateEntity?> = dao.getLatestGoldRate()
    val latestSilverRate: Flow<SilverRateEntity?> = dao.getLatestSilverRate()
    
    fun getCustomerById(id: Long): Flow<CustomerEntity?> = dao.getCustomerById(id)
    fun searchCustomers(query: String): Flow<List<CustomerEntity>> = dao.searchCustomers(query)
    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>> = dao.getOrdersByCustomer(customerId)
    fun getOrderById(orderId: Long): Flow<OrderEntity?> = dao.getOrderById(orderId)
    fun getPaymentsForOrder(orderId: Long): Flow<List<PaymentEntity>> = dao.getPaymentsForOrder(orderId)
    fun getDeliveriesForOrder(orderId: Long): Flow<List<DeliveryEntity>> = dao.getDeliveriesForOrder(orderId)
    fun getNotesForOrder(orderId: Long): Flow<List<OrderNoteEntity>> = dao.getNotesForOrder(orderId)
    
    fun getAllGoldRates(): Flow<List<GoldRateEntity>> = dao.getAllGoldRates()
    fun getAllSilverRates(): Flow<List<SilverRateEntity>> = dao.getAllSilverRates()
    fun getAllPayments(): Flow<List<PaymentEntity>> = dao.getAllPayments()
    fun getAllDeliveries(): Flow<List<DeliveryEntity>> = dao.getAllDeliveries()

    // Seeds initialization
    suspend fun initializeSeeds() = withContext(Dispatchers.IO) {
        // Core users seed
        if (dao.getUser("admin") == null) {
            dao.insertUser(UserEntity("admin", "Admin (Proprietor)", "Admin"))
        }
        if (dao.getUser("staff") == null) {
            dao.insertUser(UserEntity("staff", "Staff Representative", "Staff"))
        }

        // Initialize rates if empty
        val currentGold = dao.getLatestGoldRate().firstOrNull()
        if (currentGold == null) {
            dao.insertGoldRate(
                GoldRateEntity(
                    rate22K = 115000.0,
                    rate21K = 110000.0,
                    rate18K = 95000.0
                )
            )
        }
        
        val currentSilver = dao.getLatestSilverRate().firstOrNull()
        if (currentSilver == null) {
            dao.insertSilverRate(
                SilverRateEntity(
                    rate925 = 2100.0,
                    rate900 = 1800.0,
                    rateOther = 1500.0
                )
            )
        }
    }

    // Mutator functions
    suspend fun saveCustomer(customer: CustomerEntity): Long = withContext(Dispatchers.IO) {
        if (customer.id == 0L) {
            dao.insertCustomer(customer)
        } else {
            dao.updateCustomer(customer)
            customer.id
        }
    }

    suspend fun getCustomerByMobile(mobile: String): CustomerEntity? = withContext(Dispatchers.IO) {
        dao.getCustomerByMobile(mobile)
    }

    suspend fun deleteCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) {
        dao.deleteCustomer(customer)
    }

    suspend fun createOrder(order: OrderEntity): Long = withContext(Dispatchers.IO) {
        dao.insertOrder(order)
    }

    suspend fun updateOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        dao.updateOrder(order)
    }

    suspend fun addPayment(payment: PaymentEntity): Long = withContext(Dispatchers.IO) {
        val paymentId = dao.insertPayment(payment)
        // Refresh Order advancePaid in cached memory/DB
        // First retrieve corresponding order
        val orderFlow = dao.getOrderById(payment.orderId)
        val order = orderFlow.firstOrNull()
        if (order != null) {
            val updatedAdvancePaid = order.advancePaid + payment.advanceAmount
            val updatedOrder = order.copy(advancePaid = updatedAdvancePaid)
            dao.updateOrder(updatedOrder)
        }
        paymentId
    }

    suspend fun addDelivery(delivery: DeliveryEntity): Long = withContext(Dispatchers.IO) {
        dao.insertDelivery(delivery)
    }

    suspend fun saveGoldRate(rate: GoldRateEntity): Long = withContext(Dispatchers.IO) {
        dao.insertGoldRate(rate)
    }

    suspend fun saveSilverRate(rate: SilverRateEntity): Long = withContext(Dispatchers.IO) {
        dao.insertSilverRate(rate)
    }

    suspend fun addOrderNote(note: OrderNoteEntity): Long = withContext(Dispatchers.IO) {
        dao.insertOrderNote(note)
    }
}
