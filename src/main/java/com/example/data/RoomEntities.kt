package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [Index(value = ["mobile"], unique = true)]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photo: String? = null,
    val name: String,
    val mobile: String,
    val district: String,
    val thana: String,
    val village: String,
    val address: String,
    val fingerSize: String,
    val ringSize: String,
    val handSize: String,
    val notes: String,
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val orderDate: Long = System.currentTimeMillis(),
    val jewelryType: String, // "Gold" or "Silver"
    val productType: String, // "Ring", "Chain", "Necklace", etc.
    val designImage: String? = null,
    val orderSource: String, // "Messenger", "WhatsApp", "IMO", "TikTok", "Physical Shop"
    val specialNotes: String,
    val orderStatus: String, // "New Order", "Confirmed", "In Production", "Ready", "Delivered", "Cancelled"
    
    // Weight specifications
    val karatOrGrade: String, // e.g. "22 Karat", "925 Silver"
    val weightVori: Double,
    val weightAna: Double,
    val weightRati: Double,
    val ratePerVori: Double,
    
    // Financial calculations
    val makingCharge: Double,
    val stoneCost: Double,
    val otherCost: Double,
    val totalBill: Double,
    val advancePaid: Double // Kept updated for convenience
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val advanceAmount: Double,
    val advanceDate: Long = System.currentTimeMillis(),
    val paymentMethod: String, // "Bkash", "Nagad", "Bank Transfer", "Cash", "Other"
    val remainingDue: Double,
    val paymentNotes: String
)

@Entity(
    tableName = "deliveries",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class DeliveryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val parcelId: String,
    val courierName: String,
    val deliveryDate: Long = System.currentTimeMillis(),
    val trackingNotes: String
)

@Entity(tableName = "gold_rates")
data class GoldRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rateDate: Long = System.currentTimeMillis(),
    val rate22K: Double,
    val rate21K: Double,
    val rate18K: Double
)

@Entity(tableName = "silver_rates")
data class SilverRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rateDate: Long = System.currentTimeMillis(),
    val rate925: Double,
    val rate900: Double,
    val rateOther: Double
)

@Entity(
    tableName = "order_notes",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val author: String, // "Admin" or "Staff"
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // "admin" or "staff"
    val name: String,
    val role: String, // "Admin", "Staff"
    val lastActive: Long = System.currentTimeMillis()
)
