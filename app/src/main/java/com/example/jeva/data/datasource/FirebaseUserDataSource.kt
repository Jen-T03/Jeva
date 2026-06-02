package com.example.jeva.data.datasource

import com.example.jeva.domain.model.TransactionModel
import com.example.jeva.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    suspend fun authenticateWithFirebase(email: String, javaPass: String): User {
        val authResult = auth.signInWithEmailAndPassword(email, javaPass).await()
        val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

        val snapshot = database.reference.child("users").child(uid).get().await()
        return snapshot.getValue(User::class.java) ?: throw Exception("Error al mapear datos del usuario")
    }

    suspend fun registerInFirebase(name: String, email: String, javaPass: String, frontPath: String, backPath: String): User {
        val authResult = auth.createUserWithEmailAndPassword(email, javaPass).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo crear el usuario")

        val newUser = User(
            id = uid,
            name = name,
            email = email,
            balance = 1500000.0,
            documentFrontUrl = frontPath,
            documentBackUrl = backPath,
            transactions = emptyMap()
        )

        database.reference.child("users").child(uid).setValue(newUser).await()
        return newUser
    }

    suspend fun addTransaction(uid: String, currentBalance: Double, transaction: TransactionModel) {
        val userRef = database.reference.child("users").child(uid)
        userRef.child("balance").setValue(currentBalance).await()
        val newTxRef = userRef.child("transactions").push()
        val finalTx = transaction.copy(id = newTxRef.key ?: "")
        newTxRef.setValue(finalTx).await()
    }


    suspend fun transferToEmail(
        senderUid: String,
        senderBalance: Double,
        recipientEmail: String,
        amount: Double,
        date: String
    ) {

        val snapshot = database.reference.child("users")
            .orderByChild("email")
            .equalTo(recipientEmail)
            .get()
            .await()

        if (!snapshot.exists()) {
            throw Exception("No existe un usuario con ese correo.")
        }


        val recipientSnapshot = snapshot.children.first()
        val recipientUid = recipientSnapshot.key ?: throw Exception("Error al obtener destinatario.")
        val recipientBalance = recipientSnapshot.child("balance").getValue(Double::class.java)
            ?: throw Exception("Error al obtener saldo del destinatario.")


        val senderRef = database.reference.child("users").child(senderUid)
        senderRef.child("balance").setValue(senderBalance - amount).await()


        val senderTxRef = senderRef.child("transactions").push()
        senderTxRef.setValue(
            TransactionModel(
                id = senderTxRef.key ?: "",
                description = "Envío a $recipientEmail",
                amount = -amount,
                date = date,
                isExpense = true
            )
        ).await()


        val recipientRef = database.reference.child("users").child(recipientUid)
        recipientRef.child("balance").setValue(recipientBalance + amount).await()


        val recipientTxRef = recipientRef.child("transactions").push()
        val senderEmail = auth.currentUser?.email ?: "desconocido"
        recipientTxRef.setValue(
            TransactionModel(
                id = recipientTxRef.key ?: "",
                description = "Recibido de $senderEmail",
                amount = amount,
                date = date,
                isExpense = false
            )
        ).await()
    }

    fun logout() {
        auth.signOut()
    }
}