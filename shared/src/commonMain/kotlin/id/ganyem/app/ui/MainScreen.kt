
package id.ganyem.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

enum class MainDestination {
    HOME,
    ORDERS,
    ITEMS,
    OTHERS
}

@Composable
fun MainScreen(
    onCreateOrder: () -> Unit,
    homeContent: @Composable () -> Unit,
    ordersContent: @Composable () -> Unit,
    itemsContent: @Composable () -> Unit,
    othersContent: @Composable () -> Unit
) {
    var destination by rememberSaveable {
        mutableStateOf(MainDestination.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = destination == MainDestination.HOME,
                        onClick = {
                            destination = MainDestination.HOME
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = destination == MainDestination.ORDERS,
                        onClick = {
                            destination = MainDestination.ORDERS
                        },
                        icon = {
                            Icon(
                                Icons.Default.ListAlt,
                                contentDescription = "Orders"
                            )
                        },
                        label = { Text("Orders") }
                    )

                    // Slot tengah untuk tombol Create Order.
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = onCreateOrder
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create Order"
                            )
                        }
                    }

                    NavigationBarItem(
                        selected = destination == MainDestination.ITEMS,
                        onClick = {
                            destination = MainDestination.ITEMS
                        },
                        icon = {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = "Items"
                            )
                        },
                        label = { Text("Items") }
                    )

                    NavigationBarItem(
                        selected = destination == MainDestination.OTHERS,
                        onClick = {
                            destination = MainDestination.OTHERS
                        },
                        icon = {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "Others"
                            )
                        },
                        label = { Text("Others") }
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
            when (destination) {
                MainDestination.HOME -> homeContent()
                MainDestination.ORDERS -> ordersContent()
                MainDestination.ITEMS -> itemsContent()
                MainDestination.OTHERS -> othersContent()
            }
        }
    }
}
