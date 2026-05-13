package com.example.jeva.data.repository

import com.example.jeva.data.model.LoginUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//Gestiona la autenticación y persistencia de datos de usuario.
class AuthRepository {
    // Instancias oficiales de Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    //Crea un usuario en Authentication y su perfil

    fun registrarUsuario(
        correo: String,
        clave: String,
        datos: LoginUser,
        onResult: (Boolean, String?) -> Unit
    ) {
        // Crear el login
        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnSuccessListener { resultado ->
                val uid = resultado.user?.uid ?: ""
                // Si el login es exitoso, guardamos los datos extra en Firestore
                guardarDatosEnNube(uid, datos, onResult)
            }
            .addOnFailureListener { error ->
                onResult(false, error.message)
            }
    }


    //Guarda la información del perfil (nombre, saldo) en la base de datos

    private fun guardarDatosEnNube(uid: String, usuario: LoginUser, onResult: (Boolean, String?) -> Unit) {
        db.collection("usuarios").document(uid).set(usuario)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { error -> onResult(false, error.message) }
    }


     // Valida credenciales para el inicio de sesión

    fun iniciarSesion(correo: String, clave: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(correo, clave)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { error -> onResult(false, error.message) }
    }
}