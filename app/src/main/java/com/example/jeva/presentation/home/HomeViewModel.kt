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
    var recipientEmail by mutableStateOf("")
    var message by mutableStateOf("")

    init {
        listenToUserData()
    }

    private fun listenToUserData() {
        val uid = auth.currentUser?.uid ?: return
        database.reference.child("users").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    balance = snapshot.child("balance").getValue(Double::class.java) ?: 0.0

                    val txs = mutableListOf<TransactionModel>()
                    for (child in snapshot.child("transactions").children) {
                        child.getValue(TransactionModel::class.java)?.let { txs.add(it) }
                    }
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

        if (recipientEmail.isBlank()) {
            message = "Ingresa el correo del destinatario."
            return
        }
        if (transfer == null || transfer <= 0) {
            message = "Ingresa un monto válido."
            return
        }
        if (transfer > balance) {
            message = "Saldo insuficiente."
            return
        }

        val fullEmail = "${recipientEmail.trim()}@gmail.com"
        val currentUserEmail = auth.currentUser?.email ?: ""

        if (fullEmail == currentUserEmail) {
            message = "No puedes transferirte a ti mismo."
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        viewModelScope.launch {
            try {
                dataSource.transferToEmail(
                    senderUid = uid,
                    senderBalance = balance,
                    recipientEmail = fullEmail,
                    amount = transfer,
                    date = currentDate
                )
                message = "¡Transferencia exitosa a $fullEmail!"
                sendAmount = ""
                recipientEmail = ""
            } catch (e: Exception) {
                message = e.message ?: "Error al transferir."
            }
        }
    }

    fun logout() {
        dataSource.logout()
    }
}