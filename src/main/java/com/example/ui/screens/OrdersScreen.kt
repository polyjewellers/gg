package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.Lang
import com.example.ui.JewelleryViewModel
import com.example.ui.Screen
import com.example.data.OrderWithCustomer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    
    // Status Filter selections
    var selectedStatusFilter by remember { mutableStateOf("All") }
    val statuses = listOf("All", "New Order", "Confirmed", "In Production", "Ready", "Delivered", "Cancelled")
    var isFilterDropdownExpanded by remember { mutableStateOf(false) }

    val filteredOrders = if (selectedStatusFilter == "All") {
        orders
    } else {
        orders.filter { it.order.orderStatus == selectedStatusFilter }
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App top row with Create Order option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Lang.tabOrders,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.AddEditOrder()) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("add_order_fab"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Create Order")
            }
        }

        // Dropdown Status filter trigger row
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isFilterDropdownExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Status: ${if (selectedStatusFilter == "All") Lang.allLabel else selectedStatusFilter}")
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
            }

            DropdownMenu(
                expanded = isFilterDropdownExpanded,
                onDismissRequest = { isFilterDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                statuses.forEach { statusOption ->
                    DropdownMenuItem(
                        text = { Text(if (statusOption == "All") Lang.allLabel else statusOption) },
                        onClick = {
                            selectedStatusFilter = statusOption
                            isFilterDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Orders List panel
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = Lang.get("No orders found.", "কোনো অর্ডার পাওয়া যায়নি।"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { orderWithCustomer ->
                    OrderItemCard(
                        orderWithCustomer = orderWithCustomer,
                        dateFormat = dateFormat,
                        onClick = { viewModel.navigateTo(Screen.OrderDetail(orderWithCustomer.order.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    orderWithCustomer: OrderWithCustomer,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val order = orderWithCustomer.order
    val remainingDue = (order.totalBill - order.advancePaid).coerceAtLeast(0.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("order_item_card_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.productType} (${order.jewelryType})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customer: ${orderWithCustomer.customerName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusBadge(status = order.orderStatus)
            }

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weight: ${order.weightVori} vori, ${order.weightAna} ana, ${order.weightRati} rati",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rate: ৳${NumberFormat.getNumberInstance().format(order.ratePerVori)} (${order.karatOrGrade})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "৳" + NumberFormat.getNumberInstance().format(order.totalBill),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4D3E)
                    )
                    if (remainingDue > 0.0) {
                        Text(
                            text = "Due: ৳" + NumberFormat.getNumberInstance().format(remainingDue),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9E2A2B),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Paid",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1B4D3E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${order.orderSource}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(Date(order.orderDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
