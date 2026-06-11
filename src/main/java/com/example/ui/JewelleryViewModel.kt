package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    object Customers : Screen()
    data class CustomerDetail(val customerId: Long) : Screen()
    data class AddEditCustomer(val customerId: Long? = null) : Screen()
    object Orders : Screen()
    data class OrderDetail(val orderId: Long) : Screen()
    data class AddEditOrder(val customerId: Long? = null, val orderId: Long? = null, val isRepeat: Boolean = false) : Screen()
    object RateManager : Screen()
    object Reports : Screen()
}

class JewelleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JewelleryRepository
    
    // UI Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen navigation stack/history for simple back press
    private val screenHistory = mutableListOf<Screen>()

    // Global Language Toggle state
    private val _isEnglish = MutableStateFlow(true)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()

    // Global Active User Role (for role permissions)
    private val _currentRole = MutableStateFlow("Admin") // "Admin" or "Staff"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // Data lists from Repository
    val allCustomers: StateFlow<List<CustomerEntity>>
    val allOrders: StateFlow<List<OrderWithCustomer>>
    val latestGoldRate: StateFlow<GoldRateEntity?>
    val latestSilverRate: StateFlow<SilverRateEntity?>

    // Filter states for search/reports
    val globalSearchQuery = MutableStateFlow("")
    val searchResults: StateFlow<List<CustomerEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = JewelleryRepository(database.databaseDao())

        // Initializing background seed
        viewModelScope.launch {
            repository.initializeSeeds()
        }

        // Setup reactive variables
        allCustomers = repository.allCustomers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allOrders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        latestGoldRate = repository.latestGoldRate.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        latestSilverRate = repository.latestSilverRate.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Combine search query and all customers reactively
        searchResults = globalSearchQuery
            .debounce(200)
            .flatMapLatest { query ->
                if (query.trim().isEmpty()) {
                    allCustomers
                } else {
                    repository.searchCustomers(query.trim())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Initialize dictionary language
        Lang.setLanguage(true)
    }

    fun toggleLanguage() {
        val nextLanguage = !_isEnglish.value
        _isEnglish.value = nextLanguage
        Lang.setLanguage(nextLanguage)
    }

    fun setRole(role: String) {
        if (role == "Admin" || role == "Staff") {
            _currentRole.value = role
        }
    }

    fun navigateTo(screen: Screen) {
        if (screen != _currentScreen.value) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            val last = screenHistory.removeAt(screenHistory.size - 1)
            _currentScreen.value = last
            return true
        }
        return false
    }

    // --- MUTATORS & BUSINESS ACTIONS ---
    
    // Save Customer
    suspend fun saveCustomer(customer: CustomerEntity): Result<Long> {
        return try {
            // Check if mobile already exists if it's a new customer
            val existing = repository.getCustomerByMobile(customer.mobile)
            if (existing != null && existing.id != customer.id) {
                Result.failure(Exception("exists"))
            } else {
                val id = repository.saveCustomer(customer)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete Customer
    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    // Register Order
    suspend fun createOrder(order: OrderEntity): Long {
        val orderId = repository.createOrder(order)
        // Also register initial payment as advance details if advance paid is greater than zero
        if (order.advancePaid > 0.0) {
            repository.addPayment(
                PaymentEntity(
                    orderId = orderId,
                    advanceAmount = order.advancePaid,
                    paymentMethod = "Cash", // Default initial method
                    remainingDue = order.totalBill - order.advancePaid,
                    paymentNotes = "Initial Booking Advance / প্রথম বুকিং অগ্রিম"
                )
            )
        }
        return orderId
    }

    // Modify/Update Order Status
    fun updateOrderStatus(order: OrderEntity, status: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(orderStatus = status))
        }
    }

    // Add Additional Payments to Order
    fun addPaymentForOrder(orderId: Long, amount: Double, method: String, notes: String) {
        viewModelScope.launch {
            // Fetch current order values to calculate remaining due
            val currentOrder = repository.getOrderById(orderId).firstOrNull() ?: return@launch
            val totalBill = currentOrder.totalBill
            val currentAdvance = currentOrder.advancePaid + amount
            val remainingDue = (totalBill - currentAdvance).coerceAtLeast(0.0)
            
            val payment = PaymentEntity(
                orderId = orderId,
                advanceAmount = amount,
                paymentMethod = method,
                remainingDue = remainingDue,
                paymentNotes = notes
            )
            repository.addPayment(payment)
        }
    }

    // Add Courier Delivery Details
    fun addDeliveryForOrder(orderId: Long, courier: String, parcelId: String, notes: String) {
        viewModelScope.launch {
            val delivery = DeliveryEntity(
                orderId = orderId,
                courierName = courier,
                parcelId = parcelId,
                trackingNotes = notes
            )
            repository.addDelivery(delivery)
            
            // Auto update order status to "Delivered" when delivery is registered
            val currentOrder = repository.getOrderById(orderId).firstOrNull()
            if (currentOrder != null) {
                repository.updateOrder(currentOrder.copy(orderStatus = "Delivered"))
            }
        }
    }

    // Update Daily Metal Rates (Admin only)
    fun updateGoldRates(r22: Double, r21: Double, r18: Double) {
        if (_currentRole.value != "Admin") return
        viewModelScope.launch {
            repository.saveGoldRate(
                GoldRateEntity(
                    rate22K = r22,
                    rate21K = r21,
                    rate18K = r18
                )
            )
        }
    }

    fun updateSilverRates(r925: Double, r900: Double, rOther: Double) {
        if (_currentRole.value != "Admin") return
        viewModelScope.launch {
            repository.saveSilverRate(
                SilverRateEntity(
                    rate925 = r925,
                    rate900 = r900,
                    rateOther = rOther
                )
            )
        }
    }

    // --- READ STREAM PROVIDERS (HELPERS FOR SCREEN LAYOUTS) ---
    fun getCustomerStream(id: Long): Flow<CustomerEntity?> = repository.getCustomerById(id)
    fun getCustomerOrdersStream(customerId: Long): Flow<List<OrderEntity>> = repository.getOrdersByCustomer(customerId)
    fun getOrderStream(id: Long): Flow<OrderEntity?> = repository.getOrderById(id)
    fun getOrderPaymentsStream(id: Long): Flow<List<PaymentEntity>> = repository.getPaymentsForOrder(id)
    fun getOrderDeliveriesStream(id: Long): Flow<List<DeliveryEntity>> = repository.getDeliveriesForOrder(id)
    fun getOrderNotesStream(id: Long): Flow<List<OrderNoteEntity>> = repository.getNotesForOrder(id)
    
    fun getHistoryGoldRates(): Flow<List<GoldRateEntity>> = repository.getAllGoldRates()
    fun getHistorySilverRates(): Flow<List<SilverRateEntity>> = repository.getAllSilverRates()

    // Add direct comment/note to Order
    fun addOrderComment(orderId: Long, noteText: String) {
        viewModelScope.launch {
            repository.addOrderNote(
                OrderNoteEntity(
                    orderId = orderId,
                    author = _currentRole.value,
                    noteText = noteText
                )
            )
        }
    }

    // Weight helper calculation: converting Vori-Ana-Rati to numeric Vori equivalent
    fun calculateVoriWeight(vori: Double, ana: Double, rati: Double): Double {
        return vori + (ana / 16.0) + (rati / 96.0)
    }
}
