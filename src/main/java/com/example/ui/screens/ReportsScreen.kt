package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
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
import com.example.data.OrderWithCustomer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val orders by viewModel.allOrders.collectAsState()

    // Active Permission constraints
    if (currentRole != "Admin") {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Access Denied",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Access Denied / সংরক্ষিত",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = Lang.roleRestrictionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        return
    }

    var selectedMetalFilter by remember { mutableStateOf("All") }
    var selectedProductFilter by remember { mutableStateOf("All") }
    
    val productTypes = listOf("All", "Ring", "Chain", "Necklace", "Bangle", "Bracelet", "Earring", "Pendant", "Nose Pin", "Custom Design")
    val metalTypes = listOf("All", "Gold", "Silver")

    var metalExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }

    // Run custom filters on database listings
    val filteredOrders = orders.filter { item ->
        val matchesMetal = selectedMetalFilter == "All" || item.order.jewelryType.lowercase() == selectedMetalFilter.lowercase()
        val matchesProduct = selectedProductFilter == "All" || item.order.productType.lowercase().contains(selectedProductFilter.lowercase())
        matchesMetal && matchesProduct
    }

    // Totals calculations
    val totalRevenue = filteredOrders.sumOf { it.order.totalBill }
    val totalAdvance = filteredOrders.sumOf { it.order.advancePaid }
    val totalOutstanding = (totalRevenue - totalAdvance).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = Lang.reportsHeader,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Lang.get("Query shop business outcomes and exports", "দোকানের সর্বমোট ক্রয়-বিক্রয় খতিয়ান খতিয়ে দেখুন"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Dropdown Filter rows
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Filters", fontWeight = FontWeight.Bold)
                    
                    // Metal Filter Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { metalExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Metal: $selectedMetalFilter")
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(10.dp)) // subtle indicator
                            }
                            DropdownMenu(
                                expanded = metalExpanded,
                                onDismissRequest = { metalExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.45f)
                            ) {
                                metalTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedMetalFilter = type
                                            metalExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Product Filter Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { productExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Product: $selectedProductFilter")
                            }
                            DropdownMenu(
                                expanded = productExpanded,
                                onDismissRequest = { productExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.45f)
                            ) {
                                productTypes.forEach { prod ->
                                    DropdownMenuItem(
                                        text = { Text(prod) },
                                        onClick = {
                                            selectedProductFilter = prod
                                            productExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ledger totals sheet
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = Lang.statsReportTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = Lang.totalRevenue, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(totalRevenue), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = Lang.statsTotalAdvance, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(totalAdvance), fontWeight = FontWeight.Medium)
                    }
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = Lang.totalOutstanding, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(totalOutstanding), fontWeight = FontWeight.Bold, color = Color(0xFF9E2A2B), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        // Integration Sheet synchronization mappings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
                        Text(text = Lang.googleSheetsSync, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(text = Lang.sheetsStatusOk, style = MaterialTheme.typography.bodySmall)
                    
                    Button(
                        onClick = {
                            // Generate CSV snippet and trigger system share representation
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = Lang.copySheetsCsv)
                    }
                }
            }
        }

        // Listings filtered items
        item {
            Text(text = "Transactions Ledger (${filteredOrders.size})", fontWeight = FontWeight.Bold)
        }

        if (filteredOrders.isEmpty()) {
            item {
                Text(text = "No matching records found.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(filteredOrders) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "${entry.order.productType} [${entry.order.jewelryType}]", fontWeight = FontWeight.Bold)
                            Text(text = "By: ${entry.customerName}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(text = "৳${NumberFormat.getNumberInstance().format(entry.order.totalBill)}", fontWeight = FontWeight.Bold, color = Color(0xFF1B4D3E))
                    }
                }
            }
        }
    }
}
