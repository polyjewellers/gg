package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Lang
import com.example.ui.JewelleryViewModel
import com.example.ui.Screen
import com.example.data.CustomerEntity
import com.example.data.OrderEntity
import com.example.data.GoldRateEntity
import com.example.data.SilverRateEntity
import kotlinx.coroutines.launch
import java.text.NumberFormat

// --- REGISTER & EDIT CUSTOMER FORM ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerForm(
    customerId: Long?,
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val allCustomersList by viewModel.allCustomers.collectAsState()
    
    val editingCustomer = remember(customerId, allCustomersList) {
        allCustomersList.find { it.id == customerId }
    }

    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var fingerSize by remember { mutableStateOf("") }
    var ringSize by remember { mutableStateOf("") }
    var handSize by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Prefill if editing
    LaunchedEffect(editingCustomer) {
        editingCustomer?.let {
            name = it.name
            mobile = it.mobile
            district = it.district
            thana = it.thana
            village = it.village
            address = it.address
            fingerSize = it.fingerSize
            ringSize = it.ringSize
            handSize = it.handSize
            notes = it.notes
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    text = if (customerId == null) Lang.addNewCustomer else Lang.editCustomer,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (errorMsg != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it ; errorMsg = null },
                label = { Text(Lang.fullName) },
                modifier = Modifier.fillMaxWidth().testTag("customer_name_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it ; errorMsg = null },
                label = { Text(Lang.mobileNumber) },
                modifier = Modifier.fillMaxWidth().testTag("customer_mobile_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text(Lang.district) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = thana,
                onValueChange = { thana = it },
                label = { Text(Lang.thana) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text(Lang.village) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(Lang.fullAddress) },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fingerSize,
                    onValueChange = { fingerSize = it },
                    label = { Text(Lang.fingerSize) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ringSize,
                    onValueChange = { ringSize = it },
                    label = { Text(Lang.ringSize) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = handSize,
                    onValueChange = { handSize = it },
                    label = { Text(Lang.handSize) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(Lang.notes) },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
        }

        item {
            Button(
                onClick = {
                    if (name.trim().isEmpty() || mobile.trim().isEmpty()) {
                        errorMsg = "Name and Phone values cannot be empty!"
                        return@Button
                    }
                    val customer = CustomerEntity(
                        id = customerId ?: 0L,
                        name = name.trim(),
                        mobile = mobile.trim(),
                        district = district.trim(),
                        thana = thana.trim(),
                        village = village.trim(),
                        address = address.trim(),
                        fingerSize = fingerSize.trim(),
                        ringSize = ringSize.trim(),
                        handSize = handSize.trim(),
                        notes = notes.trim()
                    )
                    coroutineScope.launch {
                        val result = viewModel.saveCustomer(customer)
                        result.onSuccess {
                            viewModel.navigateBack()
                        }.onFailure {
                            if (it.message == "exists") {
                                errorMsg = Lang.errorMobileExists
                            } else {
                                errorMsg = "Database error saving customer info!"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_customer_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Lang.saveCustomer, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


// --- DYNAMIC REGISTER ORDER FORM WITH AUTOMATED PRICING CALCULATOR ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOrderForm(
    prefilledCustomerId: Long?,
    editingOrderId: Long?,
    isRepeat: Boolean,
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    val allCustomers by viewModel.allCustomers.collectAsState()
    val goldRatesLatest by viewModel.latestGoldRate.collectAsState()
    val silverRatesLatest by viewModel.latestSilverRate.collectAsState()

    // Form attributes state
    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var jewelryType by remember { mutableStateOf("Gold") } // "Gold" or "Silver"
    var productType by remember { mutableStateOf("Ring") }
    var orderSource by remember { mutableStateOf("Physical Shop") }
    
    // Purity specs
    var karatOrGrade by remember { mutableStateOf("22 Karat") }
    
    // Weights
    var weightVori by remember { mutableStateOf("") }
    var weightAna by remember { mutableStateOf("") }
    var weightRati by remember { mutableStateOf("") }
    
    // Additional Costs
    var makingCharge by remember { mutableStateOf("") }
    var stoneCost by remember { mutableStateOf("") }
    var otherCost by remember { mutableStateOf("") }
    
    // Deposit / Advance Payment
    var advancePaidAmt by remember { mutableStateOf("") }
    
    var specialNotes by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Prefill customer metadata on load
    LaunchedEffect(prefilledCustomerId, allCustomers) {
        if (prefilledCustomerId != null) {
            val targeted = allCustomers.find { it.id == prefilledCustomerId }
            selectedCustomer = targeted
            
            // Repeat Order requirement: if repeating, prefill details from customer metadata automatically
            if (isRepeat && targeted != null) {
                specialNotes = "Repeat Booking of ${targeted.name}. Sizing settings: Finger - ${targeted.fingerSize}, Ring - ${targeted.ringSize}."
            }
        }
    }

    // Dynamic price calculation formulas inside Compose state reactively!
    val ratesByKarat = remember(goldRatesLatest, silverRatesLatest, jewelryType, karatOrGrade) {
        if (jewelryType == "Gold") {
            val gold = goldRatesLatest ?: GoldRateEntity(rate22K = 115000.0, rate21K = 110000.0, rate18K = 95000.0)
            when (karatOrGrade) {
                "22 Karat" -> gold.rate22K
                "21 Karat" -> gold.rate21K
                "18 Karat" -> gold.rate18K
                else -> gold.rate22K
            }
        } else {
            val silver = silverRatesLatest ?: SilverRateEntity(rate925 = 2100.0, rate900 = 1800.0, rateOther = 1500.0)
            when (karatOrGrade) {
                "925 Silver" -> silver.rate925
                "900 Silver" -> silver.rate900
                "Other" -> silver.rateOther
                else -> silver.rate925
            }
        }
    }

    val liveMetalPrice = remember(weightVori, weightAna, weightRati, ratesByKarat) {
        val v = weightVori.toDoubleOrNull() ?: 0.0
        val a = weightAna.toDoubleOrNull() ?: 0.0
        val r = weightRati.toDoubleOrNull() ?: 0.0
        val decimalVori = viewModel.calculateVoriWeight(v, a, r)
        decimalVori * ratesByKarat
    }

    val liveTotalBill = remember(liveMetalPrice, makingCharge, stoneCost, otherCost) {
        val mc = makingCharge.toDoubleOrNull() ?: 0.0
        val sc = stoneCost.toDoubleOrNull() ?: 0.0
        val oc = otherCost.toDoubleOrNull() ?: 0.0
        liveMetalPrice + mc + sc + oc
    }

    val liveDueRemaining = remember(liveTotalBill, advancePaidAmt) {
        val adv = advancePaidAmt.toDoubleOrNull() ?: 0.0
        (liveTotalBill - adv).coerceAtLeast(0.0)
    }

    // Interactive Dropdowns Expand states
    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var jTypeDropdownExpanded by remember { mutableStateOf(false) }
    var prodDropdownExpanded by remember { mutableStateOf(false) }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var purityDropdownExpanded by remember { mutableStateOf(false) }

    // Preset options
    val prodOptions = listOf("Ring", "Chain", "Necklace", "Bangle", "Bracelet", "Earring", "Pendant", "Nose Pin", "Custom Design")
    val sourceOptions = listOf("Messenger", "WhatsApp", "IMO", "TikTok", "Physical Shop")
    val goldKaratOptions = listOf("22 Karat", "21 Karat", "18 Karat")
    val silverGradeOptions = listOf("925 Silver", "900 Silver", "Other")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    text = Lang.addNewOrder,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (errorMsg != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(text = errorMsg!!, color = MaterialTheme.colorScheme.onError, modifier = Modifier.padding(12.dp))
                }
            }
        }

        // 1. SELECT REGISTERED CLIENT DROPDOWN CONTAINER
        item {
            Text(text = Lang.selectCustomerFirst, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(4.dp))
            Box(Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { clientDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth().testTag("select_client_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = selectedCustomer?.name ?: "Click to Select Registered Customer...")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
                }

                DropdownMenu(
                    expanded = clientDropdownExpanded,
                    onDismissRequest = { clientDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    allCustomers.forEach { cust ->
                        DropdownMenuItem(
                            text = { Text("${cust.name} (${cust.mobile})") },
                            onClick = {
                                selectedCustomer = cust
                                clientDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 2. JEWELRY MATERIAL SELECTION ROW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metal type Gold / Silver dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { jTypeDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth().testTag("jewelry_type_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (jewelryType == "Gold") Lang.typeGold else Lang.typeSilver)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = jTypeDropdownExpanded,
                        onDismissRequest = { jTypeDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Gold", "Silver").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(if (item == "Gold") Lang.typeGold else Lang.typeSilver) },
                                onClick = {
                                    jewelryType = item
                                    // Reset purity defaults
                                    karatOrGrade = if (item == "Gold") "22 Karat" else "925 Silver"
                                    jTypeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Product type Ring, Bangle, Necklace... dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { prodDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth().testTag("product_category_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = productType)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = prodDropdownExpanded,
                        onDismissRequest = { prodDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        prodOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    productType = opt
                                    prodDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // 3. ORDER ACQUISITION SOURCE
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { sourceDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth().testTag("order_source_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "${Lang.orderSource}: $orderSource")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = sourceDropdownExpanded,
                    onDismissRequest = { sourceDropdownExpanded = false }
                ) {
                    sourceOptions.forEach { src ->
                        DropdownMenuItem(
                            text = { Text(src) },
                            onClick = {
                                orderSource = src
                                sourceDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 4. PURITY SPEC DECK
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { purityDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth().testTag("purity_grade_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "${Lang.karatGradeSelection}: $karatOrGrade")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = purityDropdownExpanded,
                    onDismissRequest = { purityDropdownExpanded = false }
                ) {
                    val opts = if (jewelryType == "Gold") goldKaratOptions else silverGradeOptions
                    opts.forEach { o ->
                        DropdownMenuItem(
                            text = { Text(o) },
                            onClick = {
                                karatOrGrade = o
                                purityDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 5. WEIGHT DATA FIELDS (Vori, Ana, Rati)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = Lang.weightSectionTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = weightVori,
                            onValueChange = { weightVori = it },
                            label = { Text(Lang.voriUnit) },
                            modifier = Modifier.weight(1f).testTag("weight_vori_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = weightAna,
                            onValueChange = { weightAna = it },
                            label = { Text(Lang.anaUnit) },
                            modifier = Modifier.weight(1f).testTag("weight_ana_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = weightRati,
                            onValueChange = { weightRati = it },
                            label = { Text(Lang.ratiUnit) },
                            modifier = Modifier.weight(1f).testTag("weight_rati_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // 6. VALUE ADDED CHARGES (Making charges...)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = Lang.extraCosts, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    
                    OutlinedTextField(
                        value = makingCharge,
                        onValueChange = { makingCharge = it },
                        label = { Text(Lang.makingCharge) },
                        modifier = Modifier.fillMaxWidth().testTag("making_charge_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stoneCost,
                        onValueChange = { stoneCost = it },
                        label = { Text(Lang.stoneCost) },
                        modifier = Modifier.fillMaxWidth().testTag("stone_cost_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = otherCost,
                        onValueChange = { otherCost = it },
                        label = { Text(Lang.otherMaterialCost) },
                        modifier = Modifier.fillMaxWidth().testTag("other_cost_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }

        // 7. INITIAL ADVANCE PAID / DEPOSIT AMOUNT
        item {
            OutlinedTextField(
                value = advancePaidAmt,
                onValueChange = { advancePaidAmt = it },
                label = { Text(Lang.initialAdvancePaid) },
                modifier = Modifier.fillMaxWidth().testTag("advance_booking_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        // 8. SPECIAL PRODUCTION NOTES
        item {
            OutlinedTextField(
                value = specialNotes,
                onValueChange = { specialNotes = it },
                label = { Text(Lang.orderSpecialNotes) },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }

        // 9. LIVE CONVERSION LEDGER PRICE PANEL (SHOWS AUTOMATIC CALCULATIONS)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = Lang.financeHeader, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Applied Rate (per Vori):", style = MaterialTheme.typography.bodySmall)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(ratesByKarat) + " [$karatOrGrade]", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = Lang.calculatedMetalPrice, style = MaterialTheme.typography.bodySmall)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(liveMetalPrice), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = Lang.calculatedTotalBill, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(liveTotalBill), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                    }
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = Lang.dueRemaining, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳" + NumberFormat.getNumberInstance().format(liveDueRemaining), fontWeight = FontWeight.Bold, color = Color(0xFF9E2A2B), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // 10. SUBMIT COMPONENT
        item {
            Button(
                onClick = {
                    if (selectedCustomer == null) {
                        errorMsg = "Please select a registered customer first!"
                        return@Button
                    }
                    val vVal = weightVori.toDoubleOrNull() ?: 0.0
                    val aVal = weightAna.toDoubleOrNull() ?: 0.0
                    val rVal = weightRati.toDoubleOrNull() ?: 0.0
                    if (vVal == 0.0 && aVal == 0.0 && rVal == 0.0) {
                        errorMsg = "Weights must be greater than zero!"
                        return@Button
                    }
                    
                    val order = OrderEntity(
                        id = editingOrderId ?: 0L,
                        customerId = selectedCustomer!!.id,
                        orderDate = System.currentTimeMillis(),
                        jewelryType = jewelryType,
                        productType = productType,
                        orderSource = orderSource,
                        specialNotes = specialNotes.trim(),
                        orderStatus = "New Order",
                        karatOrGrade = karatOrGrade,
                        weightVori = vVal,
                        weightAna = aVal,
                        weightRati = rVal,
                        ratePerVori = ratesByKarat,
                        makingCharge = makingCharge.toDoubleOrNull() ?: 0.0,
                        stoneCost = stoneCost.toDoubleOrNull() ?: 0.0,
                        otherCost = otherCost.toDoubleOrNull() ?: 0.0,
                        totalBill = liveTotalBill,
                        advancePaid = advancePaidAmt.toDoubleOrNull() ?: 0.0
                    )
                    coroutineScope.launch {
                        viewModel.createOrder(order)
                        viewModel.navigateTo(Screen.Orders)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_order_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = Lang.createOrderSubmit, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
