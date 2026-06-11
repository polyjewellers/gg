package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.Lang
import com.example.ui.JewelleryViewModel
import com.example.data.GoldRateEntity
import com.example.data.SilverRateEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminRatesScreen(
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()

    val goldRate by viewModel.latestGoldRate.collectAsState()
    val silverRate by viewModel.latestSilverRate.collectAsState()
    
    val goldHistory by viewModel.getHistoryGoldRates().collectAsState(initial = emptyList())
    val silverHistory by viewModel.getHistorySilverRates().collectAsState(initial = emptyList())

    // Checking roles
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

    var showingSuccessBanner by remember { mutableStateOf(false) }

    // Rates mutable inputs
    var g22 by remember { mutableStateOf("") }
    var g21 by remember { mutableStateOf("") }
    var g18 by remember { mutableStateOf("") }

    var s925 by remember { mutableStateOf("") }
    var s900 by remember { mutableStateOf("") }
    var sOther by remember { mutableStateOf("") }

    // Prefill form values once from DB state
    LaunchedEffect(goldRate) {
        if (goldRate != null) {
            g22 = goldRate!!.rate22K.toInt().toString()
            g21 = goldRate!!.rate21K.toInt().toString()
            g18 = goldRate!!.rate18K.toInt().toString()
        }
    }
    LaunchedEffect(silverRate) {
        if (silverRate != null) {
            s925 = silverRate!!.rate925.toInt().toString()
            s900 = silverRate!!.rate900.toInt().toString()
            sOther = silverRate!!.rateOther.toInt().toString()
        }
    }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = Lang.rateManagerTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Lang.latestRates,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (showingSuccessBanner) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Lang.saveSuccess,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Gold Rate Updates Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = Lang.editGoldRates,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = g22,
                        onValueChange = { g22 = it },
                        label = { Text("22 Karat (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("g22_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = g21,
                        onValueChange = { g21 = it },
                        label = { Text("21 Karat (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("g21_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = g18,
                        onValueChange = { g18 = it },
                        label = { Text("18 Karat (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("g18_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }

        // Silver Rate Updates Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = Lang.editSilverRates,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = s925,
                        onValueChange = { s925 = it },
                        label = { Text("925 Silver (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("s925_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = s900,
                        onValueChange = { s900 = it },
                        label = { Text("900 Silver (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("s900_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sOther,
                        onValueChange = { sOther = it },
                        label = { Text("Other Silver (per Vori BDT)") },
                        modifier = Modifier.fillMaxWidth().testTag("s_other_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }

        // Row containing save trigger
        item {
            Button(
                onClick = {
                    val doubleG22 = g22.toDoubleOrNull() ?: 115000.0
                    val doubleG21 = g21.toDoubleOrNull() ?: 110000.0
                    val doubleG18 = g18.toDoubleOrNull() ?: 95000.0

                    val doubleS925 = s925.toDoubleOrNull() ?: 2100.0
                    val doubleS900 = s900.toDoubleOrNull() ?: 1800.0
                    val doubleSOther = sOther.toDoubleOrNull() ?: 1500.0

                    viewModel.updateGoldRates(doubleG22, doubleG21, doubleG18)
                    viewModel.updateSilverRates(doubleS925, doubleS900, doubleSOther)
                    showingSuccessBanner = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_rates_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = Lang.saveSettings, fontWeight = FontWeight.Bold)
            }
        }

        // Historic log rows showing rates
        item {
            Text(
                text = Lang.get("Rate History Logs", "রেট আপডেটের ইতিহাস"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (goldHistory.isEmpty() && silverHistory.isEmpty()) {
            item {
                Text(
                    text = "No history recorded yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(goldHistory) { gh ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Gold Rate Update", fontWeight = FontWeight.Bold, color = Color(0xFFC5A049))
                            Text(text = dateFormat.format(Date(gh.rateDate)), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "22K: ৳${gh.rate22K} | 21K: ৳${gh.rate21K} | 18K: ৳${gh.rate18K}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
