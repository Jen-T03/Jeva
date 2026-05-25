package com.example.jeva.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeva.data.datasource.FirebaseUserDataSource
import com.example.jeva.domain.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val dataSource = FirebaseUserDataSource()

    var balance by mutableStateOf(0.0)
    var transactionList by mutableStateOf<List<TransactionModel>>(emptyList())
    var sendAmount by mutableStateOf("")
    var message by mutableStateOf("")

    init {
        listenToUserData()
    }

    private fun listenToUserData() {
        val uid = auth.currentUser?.uid ?: return
        database.reference.child("users").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balanceValue = snapshot.child("balance").getValue(Double::class.java) ?: 0.0
                    balance = balanceValue

                    val txs = mutableListOf<TransactionModel>()
                    val txSnapshot = snapshot.child("transactions")
                    for (child in txSnapshot.children) {
                        child.getValue(TransactionModel::class.java)?.let { txs.add(it) }
                    }
                    // Muestra las transacciones más recientes primero
                    transactionList = txs.reversed()
                }

                override fun onCancelled(error: DatabaseError) {
                    message = "Error de conexión: ${error.message}"
                }
            })
    }

    fun executeTransaction() {
        val transfer = sendAmount.toDoubleOrNull()
        val uid = auth.currentUser?.uid ?: return

        if (transfer == null || transfer <= 0 || transfer > balance) {
            message = "Monto inválido o saldo insuficiente."
            return
        }

        viewModelScope.launch {
            val newBalance = balance - transfer
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val newTx = TransactionModel(
                description = "Envío de dinero",
                amount = -transfer,
                date = currentDate,
                isExpense = true
            )

            try {
                dataSource.addTransaction(uid, newBalance, newTx)
                message = "¡Transacción exitosa!"
                sendAmount = ""
            } catch (e: Exception) {
                message = "Error al transferir: ${e.message}"
            }
        }
    }

    fun logout() {
        dataSource.logout()
    }
}