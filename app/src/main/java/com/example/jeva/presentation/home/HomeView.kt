package com.example.jeva.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(onLogout: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jeva Wallet") },
                actions = {
                    TextButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Saldo total disponible", style = MaterialTheme.typography.titleMedium)
                    Text("$${String.format("%,.2f", viewModel.balance)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.sendAmount,
                onValueChange = { viewModel.sendAmount = it },
                label = { Text("Monto a transferir") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { viewModel.executeTransaction() }, modifier = Modifier.fillMaxWidth()) {
                Text("Ejecutar Transacción")
            }

            if (viewModel.message.isNotEmpty()) {
                Text(viewModel.message, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Historial de Movimientos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))

            // LAZYCOLUMN VINCULADA A REALTIME DATABASE
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(viewModel.transactionList) { tx ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(tx.description, style = MaterialTheme.typography.bodyLarge)
                                Text(tx.date, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = "$${String.format("%,.0f", tx.amount)}",
                                color = if (tx.isExpense) Color.Red else Color(0xFF388E3C),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}