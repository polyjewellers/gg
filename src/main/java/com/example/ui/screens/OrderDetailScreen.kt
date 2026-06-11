package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import com.example.ui.Lang
import com.example.ui.JewelleryViewModel
import com.example.ui.Screen
import com.example.data.OrderEntity
import com.example.data.CustomerEntity
import com.example.data.PaymentEntity
import com.example.data.DeliveryEntity
import com.example.data.OrderNoteEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val orderWithCustomers by viewModel.allOrders.collectAsState()
    val orderWithCustomer = orderWithCustomers.find { it.order.id == orderId }

    val payments by viewModel.getOrderPaymentsStream(orderId).collectAsState(initial = emptyList())
    val deliveries by viewModel.getOrderDeliveriesStream(orderId).collectAsState(initial = emptyList())
    val comments by viewModel.getOrderNotesStream(orderId).collectAsState(initial = emptyList())
    val role by viewModel.currentRole.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showDeliveryDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }

    if (orderWithCustomer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val order = orderWithCustomer.order
    val remainingDue = (order.totalBill - order.advancePaid).coerceAtLeast(0.0)
    val dateFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App top navigation row
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
                    text = "Invoice Order #${order.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Summary Card with status updater
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Client: ${orderWithCustomer.customerName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mobile: ${orderWithCustomer.customerMobile}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = order.orderStatus)
                    }

                    Divider()

                    // Quick Specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Product Purity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Text(order.karatOrGrade, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Order Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Text(dateFormat.format(Date(order.orderDate)), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Direct order status state mutator dropdown box (accessible to both Admin & Staff!)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Modify Order Status / স্ট্যাটাস পরিবর্তন:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    var statusMenuExpanded by remember { mutableStateOf(false) }
                    val statusOptions = listOf("New Order", "Confirmed", "In Production", "Ready", "Delivered", "Cancelled")

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { statusMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = order.orderStatus)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
                        }

                        DropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = { statusMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.updateOrderStatus(order, option)
                                        statusMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weight Specs visual grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = Lang.weightSectionTitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeightBadge(unit = Lang.voriUnit, amt = "${order.weightVori} V")
                        WeightBadge(unit = Lang.anaUnit, amt = "${order.weightAna} A")
                        WeightBadge(unit = Lang.ratiUnit, amt = "${order.weightRati} R")
                    }
                }
            }
        }

        // Ledger Pricing Calculations Visual Card
        item {
            Text(
                text = Lang.financeHeader,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val rawWeightDecimal = viewModel.calculateVoriWeight(order.weightVori, order.weightAna, order.weightRati)
                    val formattedMetalPrice = rawWeightDecimal * order.ratePerVori

                    LedgerRow(label = "${Lang.calculatedMetalPrice} (${order.jewelryType})", value = "৳" + NumberFormat.getNumberInstance().format(formattedMetalPrice), color = MaterialTheme.colorScheme.onSurface)
                    LedgerRow(label = Lang.makingCharge, value = "৳" + NumberFormat.getNumberInstance().format(order.makingCharge), color = MaterialTheme.colorScheme.onSurface)
                    LedgerRow(label = Lang.stoneCost, value = "৳" + NumberFormat.getNumberInstance().format(order.stoneCost), color = MaterialTheme.colorScheme.onSurface)
                    LedgerRow(label = Lang.otherMaterialCost, value = "৳" + NumberFormat.getNumberInstance().format(order.otherCost), color = MaterialTheme.colorScheme.onSurface)
                    
                    Divider()
                    
                    LedgerRow(label = Lang.calculatedTotalBill, value = "৳" + NumberFormat.getNumberInstance().format(order.totalBill), color = Color(0xFF1B4D3E), isBold = true)
                    LedgerRow(label = Lang.get("Advance Paid So Far", "পরিশোধিত অগ্রিম (সর্বমোট)"), value = "৳" + NumberFormat.getNumberInstance().format(order.advancePaid), color = MaterialTheme.colorScheme.secondary)
                    
                    Divider()
                    
                    LedgerRow(label = Lang.dueRemaining, value = "৳" + NumberFormat.getNumberInstance().format(remainingDue), color = Color(0xFF9E2A2B), isBold = true)
                }
            }
        }

        // Action Trigger Rows
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Collect Payment
                Button(
                    onClick = { showPaymentDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("collect_payment_button")
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Lang.recordPayment, fontSize = 12.sp)
                }

                // Dispatch Courier Delivery
                Button(
                    onClick = { showDeliveryDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5A049)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("courier_dispatch_button")
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Lang.dispatchDelivery, fontSize = 12.sp)
                }
            }
        }

        // Order Timeline elements
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Lang.get("Chronological Event Logs", "লেনদেন ও শিপমেন্ট লগ"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Timeline: Payments logged
        if (payments.isNotEmpty()) {
            item {
                Text(
                    text = Lang.recentPayments,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            items(payments) { pay ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Amount: ৳" + NumberFormat.getNumberInstance().format(pay.advanceAmount), fontWeight = FontWeight.Bold)
                            Text(text = "Method: ${pay.paymentMethod}", style = MaterialTheme.typography.bodySmall)
                            if (pay.paymentNotes.isNotEmpty()) {
                                Text(text = "Notes: ${pay.paymentNotes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = dateFormat.format(Date(pay.advanceDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Timeline: Deliveries dispatched
        if (deliveries.isNotEmpty()) {
            item {
                Text(
                    text = Lang.recentDeliveries,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            items(deliveries) { dlv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Courier: ${dlv.courierName}", fontWeight = FontWeight.Bold)
                            Text(text = "Consignment Tracking ID: ${dlv.parcelId}", style = MaterialTheme.typography.bodySmall)
                            if (dlv.trackingNotes.isNotEmpty()) {
                                Text(text = "Dispatch details: ${dlv.trackingNotes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = dateFormat.format(Date(dlv.deliveryDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Remarks / Special order notes appended by Admins
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Production Comments & Notes / নোটস:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { showCommentDialog = true }) {
                    Icon(Icons.Default.AddComment, contentDescription = "Add Notes", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (comments.isEmpty()) {
            item {
                Text(
                    text = "No additional production remarks entered yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(comments) { comment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = comment.author, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = dateFormat.format(Date(comment.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comment.noteText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    // --- DIALOG POPUPS ---

    // 1. Payment Recording Dialog
    if (showPaymentDialog) {
        var payAmount by remember { mutableStateOf("") }
        var payMethod by remember { mutableStateOf("Bkash") }
        var payNotesText by remember { mutableStateOf("") }
        val gateways = listOf("Bkash", "Nagad", "Bank Transfer", "Cash", "Other")
        var pDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text(Lang.recordPayment) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Due Amount Remaining: ৳" + NumberFormat.getNumberInstance().format(remainingDue), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9E2A2B), fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text(Lang.advanceAmountLabel) },
                        modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
                        singleLine = true
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { pDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Gateway: $payMethod")
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
                        }

                        DropdownMenu(
                            expanded = pDropdownExpanded,
                            onDismissRequest = { pDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            gateways.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        payMethod = opt
                                        pDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = payNotesText,
                        onValueChange = { payNotesText = it },
                        label = { Text(Lang.payNotes) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amtDouble = payAmount.toDoubleOrNull() ?: 0.0
                        if (amtDouble > 0.0) {
                            viewModel.addPaymentForOrder(orderId, amtDouble, payMethod, payNotesText)
                        }
                        showPaymentDialog = false
                    },
                    modifier = Modifier.testTag("payment_submit_confirm")
                ) {
                    Text(Lang.submitPayment)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Logistics Dispatch Dialog
    if (showDeliveryDialog) {
        var courierVal by remember { mutableStateOf("Pathao Courier / এসএ পরিবহন") }
        var parcelTrackingCode by remember { mutableStateOf("") }
        var logisticsNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDeliveryDialog = false },
            title = { Text(Lang.dispatchDelivery) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = courierVal,
                        onValueChange = { courierVal = it },
                        label = { Text(Lang.courierNameLabel) },
                        modifier = Modifier.fillMaxWidth().testTag("courier_name_input")
                    )

                    OutlinedTextField(
                        value = parcelTrackingCode,
                        onValueChange = { parcelTrackingCode = it },
                        label = { Text(Lang.parcelIdLabel) },
                        modifier = Modifier.fillMaxWidth().testTag("parcel_id_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = logisticsNotes,
                        onValueChange = { logisticsNotes = it },
                        label = { Text(Lang.deliveryNotesLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addDeliveryForOrder(orderId, courierVal, parcelTrackingCode, logisticsNotes)
                        showDeliveryDialog = false
                    },
                    modifier = Modifier.testTag("delivery_submit_confirm")
                ) {
                    Text(Lang.submitDelivery)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeliveryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Comments Dialog
    if (showCommentDialog) {
        var remarkTxt by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("Add Verification Remark") },
            text = {
                OutlinedTextField(
                    value = remarkTxt,
                    onValueChange = { remarkTxt = it },
                    placeholder = { Text("Type production instructions, design preferences, or updates...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (remarkTxt.trim().isNotEmpty()) {
                            viewModel.addOrderComment(orderId, remarkTxt)
                        }
                        showCommentDialog = false
                    }
                ) {
                    Text("Save")
                }
            }
        )
    }
}

@Composable
fun WeightBadge(unit: String, amt: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(text = amt, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
