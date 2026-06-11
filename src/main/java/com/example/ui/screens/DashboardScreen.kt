package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.OrderWithCustomer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.searchResults.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()
    val role by viewModel.currentRole.collectAsState()

    // Calculate aggregated statistics
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    val todayOrdersCount = orders.count {
        try {
            val orderDateStr = sdf.format(Date(it.order.orderDate))
            orderDateStr == todayDateStr
        } catch (e: Exception) {
            false
        }
    }

    val activeOrdersCount = orders.count { 
        it.order.orderStatus != "Delivered" && it.order.orderStatus != "Cancelled" 
    }
    
    val completedOrdersCount = orders.count { it.order.orderStatus == "Delivered" }
    val goldOrdersCount = orders.count { it.order.jewelryType.lowercase() == "gold" }
    val silverOrdersCount = orders.count { it.order.jewelryType.lowercase() == "silver" }
    
    val totalCustomersCount = customers.size
    
    val totalSalesAmount = orders.sumOf { it.order.totalBill }
    val totalAdvancesAmount = orders.sumOf { it.order.advancePaid }
    val totalOutstandingDue = (totalSalesAmount - totalAdvancesAmount).coerceAtLeast(0.0)

    val formatBDT = NumberFormat.getCurrencyInstance(Locale("bn", "BD"))
    formatBDT.maximumFractionDigits = 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Business card header of Poly Jewellers
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Lang.appName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Lang.get(
                            "Fine Artisan Gold & Silver Jewel Crafts since 1998",
                            "কাস্টম স্বর্ণ ও রৌপ্য অলংকার প্রস্তুতকারক"
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Live stats panel using dynamic grid of summary items
        Text(
            text = Lang.get("Live Statistics Dashboard", "লাইভ পরিসংখ্যান ড্যাশবোর্ড"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                StatCard(
                    title = Lang.dbTodayOrders,
                    value = todayOrdersCount.toString(),
                    icon = Icons.Default.Today,
                    colorAccent = MaterialTheme.colorScheme.primary
                )
            }
            item {
                StatCard(
                    title = Lang.dbActiveOrders,
                    value = activeOrdersCount.toString(),
                    icon = Icons.Default.Pending,
                    colorAccent = Color(0xFFC5A049)
                )
            }
            item {
                StatCard(
                    title = Lang.dbCompletedOrders,
                    value = completedOrdersCount.toString(),
                    icon = Icons.Default.CheckCircle,
                    colorAccent = Color(0xFF1B4D3E)
                )
            }
            item {
                StatCard(
                    title = Lang.dbGoldOrders,
                    value = goldOrdersCount.toString(),
                    icon = Icons.Default.WorkspacePremium,
                    colorAccent = Color(0xFFD4AF37)
                )
            }
            item {
                StatCard(
                    title = Lang.dbSilverOrders,
                    value = silverOrdersCount.toString(),
                    icon = Icons.Default.Stars,
                    colorAccent = Color(0xFF8E929E)
                )
            }
            item {
                StatCard(
                    title = Lang.dbTotalCustomers,
                    value = totalCustomersCount.toString(),
                    icon = Icons.Default.People,
                    colorAccent = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                StatCard(
                    title = Lang.dbTotalSales,
                    value = "৳" + NumberFormat.getNumberInstance().format(totalSalesAmount),
                    icon = Icons.Default.Payments,
                    colorAccent = Color(0xFF1B4D3E)
                )
            }
            item {
                StatCard(
                    title = Lang.dbTotalDue,
                    value = "৳" + NumberFormat.getNumberInstance().format(totalOutstandingDue),
                    icon = Icons.Default.AssignmentLate,
                    colorAccent = Color(0xFF9E2A2B)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    colorAccent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colorAccent.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
