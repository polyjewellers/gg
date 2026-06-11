package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: JewelleryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val isEnglish by viewModel.isEnglish.collectAsState()
                val activeRole by viewModel.currentRole.collectAsState()

                var isRoleMenuExpanded by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = Lang.appName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${Lang.currentRoleLabel}$activeRole",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            },
                            actions = {
                                // 1. INSTANT LANGUAGE TOGGLE
                                Button(
                                    onClick = { viewModel.toggleLanguage() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("language_toggle")
                                ) {
                                    Text(
                                        text = if (isEnglish) "বাংলা" else "English",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // 2. INSTANT ROLE SWITCHER DROPDOWN (Allows easy evaluation of Admin vs Staff permissions!)
                                Box {
                                    IconButton(
                                        onClick = { isRoleMenuExpanded = true },
                                        modifier = Modifier.testTag("role_switcher")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = "Role Mode",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = isRoleMenuExpanded,
                                        onDismissRequest = { isRoleMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("${Lang.adminRole} Mode") },
                                            onClick = {
                                                viewModel.setRole("Admin")
                                                isRoleMenuExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("${Lang.staffRole} Mode") },
                                            onClick = {
                                                viewModel.setRole("Staff")
                                                isRoleMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.navigationBarsPadding(),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val navTabs = listOf(
                                Triple(Screen.Dashboard, Lang.tabDashboard, Icons.Default.Dashboard),
                                Triple(Screen.Customers, Lang.tabCustomers, Icons.Default.People),
                                Triple(Screen.Orders, Lang.tabOrders, Icons.Default.ShoppingBag),
                                Triple(Screen.RateManager, Lang.tabRates, Icons.Default.TrendingUp),
                                Triple(Screen.Reports, Lang.tabReports, Icons.Default.Assessment)
                            )
                            
                            navTabs.forEach { (screen, label, icon) ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.navigateTo(screen) },
                                    icon = { Icon(imageVector = icon, contentDescription = label) },
                                    label = { Text(text = label, maxLines = 1, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                            is Screen.Customers -> CustomersScreen(viewModel = viewModel)
                            is Screen.Orders -> OrdersScreen(viewModel = viewModel)
                            is Screen.RateManager -> AdminRatesScreen(viewModel = viewModel)
                            is Screen.Reports -> ReportsScreen(viewModel = viewModel)
                            is Screen.CustomerDetail -> CustomerDetailScreen(
                                customerId = (currentScreen as Screen.CustomerDetail).customerId,
                                viewModel = viewModel
                            )
                            is Screen.AddEditCustomer -> AddEditCustomerForm(
                                customerId = (currentScreen as Screen.AddEditCustomer).customerId,
                                viewModel = viewModel
                            )
                            is Screen.AddEditOrder -> {
                                val args = currentScreen as Screen.AddEditOrder
                                AddEditOrderForm(
                                    prefilledCustomerId = args.customerId,
                                    editingOrderId = args.orderId,
                                    isRepeat = args.isRepeat,
                                    viewModel = viewModel
                                )
                            }
                            is Screen.OrderDetail -> OrderDetailScreen(
                                orderId = (currentScreen as Screen.OrderDetail).orderId,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
