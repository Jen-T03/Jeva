package com.example.jeva.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeva.presentation.theme.BlackPrimary
import com.example.jeva.presentation.theme.DarkGray
import com.example.jeva.presentation.theme.RedPrimary
import com.example.jeva.presentation.theme.RedSecondary
import com.example.jeva.presentation.theme.WhiteText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(onLogout: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    Scaffold(
        containerColor = BlackPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Jeva ",
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackPrimary
                ),
                actions = {
                    TextButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Text("Salir", color = RedPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(RedSecondary, BlackPrimary)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Saldo total disponible",
                        color = WhiteText.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$${String.format("%,.2f", viewModel.balance)}",
                        color = WhiteText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo correo
            OutlinedTextField(
                value = viewModel.recipientEmail,
                onValueChange = { viewModel.recipientEmail = it },
                label = { Text("Correo destinatario", color = WhiteText.copy(alpha = 0.6f)) },
                suffix = { Text("@gmail.com", color = RedPrimary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    unfocusedBorderColor = DarkGray,
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    cursorColor = RedPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(
                value = viewModel.sendAmount,
                onValueChange = { viewModel.sendAmount = it },
                label = { Text("Monto a transferir", color = WhiteText.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    unfocusedBorderColor = DarkGray,
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    cursorColor = RedPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))


            Button(
                onClick = { viewModel.executeTransaction() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "Ejecutar Transacción",
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (viewModel.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    viewModel.message,
                    color = if (viewModel.message.contains("exitosa")) Color(0xFF4CAF50) else RedPrimary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Historial de Movimientos",
                color = WhiteText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.transactionList) { tx ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkGray)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    tx.description,
                                    color = WhiteText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    tx.date,
                                    color = WhiteText.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = if (tx.isExpense) "-$${String.format("%,.0f", Math.abs(tx.amount))}"
                                else "+$${String.format("%,.0f", tx.amount)}",
                                color = if (tx.isExpense) RedPrimary else Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}