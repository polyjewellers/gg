package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Lang
import com.example.ui.JewelleryViewModel
import com.example.ui.Screen
import com.example.data.CustomerEntity
import com.example.data.OrderEntity
import com.example.data.PaymentEntity
import com.example.data.DeliveryEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val customer by viewModel.getCustomerStream(customerId).collectAsState(initial = null)
    val orders by viewModel.getCustomerOrdersStream(customerId).collectAsState(initial = emptyList())
    val role by viewModel.currentRole.collectAsState()

    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val activeCustomer = customer!!
    
    // Financial calculations
    val totalOrdersCount = orders.size
    val activeOrdersCount = orders.count { it.orderStatus != "Delivered" && it.orderStatus != "Cancelled" }
    val completedOrdersCount = orders.count { it.orderStatus == "Delivered" }
    
    val totalBusinessValue = orders.sumOf { it.totalBill }
    val totalAdvancePaid = orders.sumOf { it.advancePaid }
    val totalRemainingDue = (totalBusinessValue - totalAdvancePaid).coerceAtLeast(0.0)

    val dateFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App bar simulation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = Lang.customerProfile,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // Admin option to Delete
                if (role == "Admin") {
                    IconButton(onClick = {
                        viewModel.deleteCustomer(activeCustomer)
                        viewModel.navigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

        // Customer Profile Meta Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        
                        Column {
                            Text(
                                text = activeCustomer.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = activeCustomer.mobile,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Divider()

                    // Visual Specs and sizing information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SizeBadge(label = Lang.fingerSize, value = activeCustomer.fingerSize.ifEmpty { "N/A" })
                        SizeBadge(label = Lang.ringSize, value = activeCustomer.ringSize.ifEmpty { "N/A" })
                        SizeBadge(label = Lang.handSize, value = activeCustomer.handSize.ifEmpty { "N/A" })
                    }

                    // Complete Address Detail
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                        Column {
                            Text(text = Lang.fullAddress, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                            Text(
                                text = "${activeCustomer.village}, Thana: ${activeCustomer.thana}, District: ${activeCustomer.district}\n${activeCustomer.address}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (activeCustomer.notes.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                            Column {
                                Text(text = Lang.notes, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = activeCustomer.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action panel row for placing Repeat Order
        item {
            Button(
                onClick = { viewModel.navigateTo(Screen.AddEditOrder(customerId = activeCustomer.id, isRepeat = true)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("repeat_order_button")
            ) {
                Icon(Icons.Default.Autorenew, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = Lang.createOrderBtn, fontWeight = FontWeight.Bold)
            }
        }

        // Financial Ledger Summary Row
        item {
            Text(
                text = Lang.get("Account Financial Summary", "আর্থিক লেনদেন সারসংক্ষেপ"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LedgerRow(label = Lang.statsTotalOrders, value = "$totalOrdersCount orders", color = MaterialTheme.colorScheme.onSurface)
                    LedgerRow(label = Lang.statsActiveOrders, value = "$activeOrdersCount active", color = Color(0xFFC5A049))
                    LedgerRow(label = Lang.statsCompletedOrders, value = "$completedOrdersCount completed", color = Color(0xFF1B4D3E))
                    LedgerRow(label = Lang.statsValue, value = "৳" + NumberFormat.getNumberInstance().format(totalBusinessValue), color = Color(0xFF1B4D3E), isBold = true)
                    LedgerRow(label = Lang.statsTotalAdvance, value = "৳" + NumberFormat.getNumberInstance().format(totalAdvancePaid), color = MaterialTheme.colorScheme.secondary)
                    Divider()
                    LedgerRow(label = Lang.statsTotalDue, value = "৳" + NumberFormat.getNumberInstance().format(totalRemainingDue), color = Color(0xFF9E2A2B), isBold = true)
                }
            }
        }

        // Chronological History entries list
        item {
            Text(
                text = Lang.timelineTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (orders.isEmpty()) {
            item {
                Text(
                    text = Lang.get("No previous orders recorded.", "পূর্বে কোনো অর্ডার তালিকাভুক্ত করা হয়নি।"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("timeline_order_card_${order.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${order.productType} (${order.jewelryType})",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = order.orderStatus)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Bill: ৳" + NumberFormat.getNumberInstance().format(order.totalBill),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Due: ৳" + NumberFormat.getNumberInstance().format((order.totalBill - order.advancePaid).coerceAtLeast(0.0)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9E2A2B),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Date: ${dateFormat.format(Date(order.orderDate))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Button(
                            onClick = { viewModel.navigateTo(Screen.OrderDetail(order.id)) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text("View Details / পেমেন্ট করুন")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SizeBadge(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LedgerRow(label: String, value: String, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val bgColor = when (status) {
        "New Order" -> Color(0xFFE2E4E9)
        "Confirmed" -> Color(0xFFE5F6FD)
        "In Production" -> Color(0xFFFFF4E5)
        "Ready" -> Color(0xFFEDF7ED)
        "Delivered" -> Color(0xFFE2F0D9)
        "Cancelled" -> Color(0xFFFCE8E6)
        else -> Color(0xFFE2E4E9)
    }
    
    val textColor = when (status) {
        "New Order" -> Color(0xFF5A5C64)
        "Confirmed" -> Color(0xFF0288D1)
        "In Production" -> Color(0xFFED6C02)
        "Ready" -> Color(0xFF2E7D32)
        "Delivered" -> Color(0xFF1B4D3E)
        "Cancelled" -> Color(0xFFC62828)
        else -> Color(0xFF5A5C64)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = textColor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
