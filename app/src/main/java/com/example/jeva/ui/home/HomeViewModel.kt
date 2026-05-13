package com.example.jeva.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Estados para la interfaz
    var nombreUsuario by mutableStateOf("Cargando...")
    var saldoDisponible by mutableStateOf(0.0)

    init {
        obtenerDatosUsuario()
    }

    private fun obtenerDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return

        // Consultamos en tiempo real para que el saldo se actualice solo
        db.collection("usuarios").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                nombreUsuario = snapshot.getString("nombre") ?: "Usuario"
                saldoDisponible = snapshot.getDouble("saldo") ?: 0.0
            }
        }
    }
}